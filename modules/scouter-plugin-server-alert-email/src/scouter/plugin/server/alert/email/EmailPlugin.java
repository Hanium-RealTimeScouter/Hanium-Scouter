/*
 *  Copyright 2016 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author Sang-Cheon Park
 */
package scouter.plugin.server.alert.email;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import hanium.util.ObjectContainer; //한이음 스카우터 시간제어 모듈

import scouter.lang.AlertLevel;
import scouter.lang.TextTypes;
import scouter.lang.TimeTypeEnum;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.pack.PerfCounterPack;
import scouter.lang.pack.XLogPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.net.RequestCmd;
import scouter.server.Configure;
import scouter.server.CounterManager;
import scouter.server.Logger;
import scouter.server.core.AgentManager;
import scouter.server.db.TextRD;
import scouter.server.netio.AgentCall;
import scouter.util.DateUtil;
import scouter.util.HashUtil;

/**
 * Scouter server plugin to send alert via email
 * 
 * 2017.05.14. revised by occidere
 * 
 * @author Sang-Cheon Park(nices96@gmail.com) on 2016. 3. 28.
 */
public class EmailPlugin {
	
	// Get singleton Configure instance from server
    final Configure conf = Configure.getInstance();
    
    private static AtomicInteger ai = new AtomicInteger(0);
    private static List<Integer> javaeeObjHashList = new ArrayList<Integer>();
    
    public EmailPlugin() {
    	if (ai.incrementAndGet() == 1) {
	    	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	    	
	    	//thread count check
	    	executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					for (int objHash : javaeeObjHashList) {
						try {
							if (AgentManager.isActive(objHash)) {
								ObjectPack objectPack = AgentManager.getAgent(objHash);
								MapPack mapPack = new MapPack();
				            	mapPack.put("objHash", objHash);
								mapPack = AgentCall.call(objectPack, RequestCmd.OBJECT_THREAD_LIST, mapPack);
								
								final int LIMIT = 30; //직접 만든 임계치. 평균 20 정도 나오는 것으로 보아 30 정도가 적절
								
								//스레드 카운트 한계치. 여기서 지정한 숫자를 넘기면 thread exceed threshold 관련 메일을 보낸다.
				        		int threadCountThreshold = conf.getInt("ext_plugin_thread_count_threshold", LIMIT);
				        		int threadCount = mapPack.getList("name").size();
				        		
				        		/* 스레드 갯수 체크 */
				        		if (threadCountThreshold != 0 && threadCount > threadCountThreshold) {
				        			AlertPack ap = new AlertPack();

				        			//스레드 개수가 임계치의 2배보다 많으면 FATAL, 적으면 WARN
				        			if(threadCount > threadCountThreshold * 2) ap.level = AlertLevel.FATAL;
				        			else ap.level = AlertLevel.WARN;
				        			
				        			ap.objHash = objHash;
				    		        ap.title = "스레드가 너무 많습니다!";
				    		        ap.message = objectPack.objName + "의 스레드 개수가 " + threadCount + "개로, 임계치("+LIMIT+"개)를 초과하였습니다!";
				    		        ap.time = System.currentTimeMillis();
				    		        ap.objType = objectPack.objType;
				    				
				    		        long limitTime = conf.getLong("thread_limit_time", 120000); //2분
				    		        long safeTime = conf.getLong("thread_safe_time", 180000); //3분
				    		        //이름 일단 objectPack.objName로 해놓음 
				    		        if(ObjectContainer.sendAlert(ap, objectPack.objName, limitTime, safeTime)) alert(ap);
				        		}
							}
						} catch (Exception e) {
							// ignore
						}
					}
				}
	    	}, 
	    	0, 5, TimeUnit.SECONDS);
    	}
	}

    /**
     * 에러 발생시 메일을 보내는 메서드<br>
     * 지정한 임계시간 초과시에만 메일을 보내도록 설정<br>
     * @param pack 에러발생 객체를 담고 있는 pack
     */
    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
    public void alert(final AlertPack pack) {
    	/* Email 발송 여부 결정 */
        if (conf.getBoolean("ext_plugin_email_send_alert", true)) {
        	
        	/* 
        	 * Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
        	 * 0으로 설정하면 모든 행동에 대해 메일이 보내지게 된다.
        	 */
        	int level = conf.getInt("ext_plugin_email_level", 0);
        	
        	/* 위에서 지정한 level 값 수준보다 낮은 에러는 메일 안보냄 */
        	if (level <= pack.level) {
        		new Thread() {
        			public void run() {
                        try {
                        	
//                        	/************** 일정 시간 경과 시 메일 보내게 하는 코드 ************/
//                        	String additionalMessage = "";
//                        	long values[], curTime = pack.time; //현재시간;
//                        	final long A_MINUTE = 60000; //1분
//                        	int tmpHash = pack.objHash; //객체의 에러 발생 시간
//                        	
//                        	//본문에 표시될 에러 발생 시간 포맷
//                        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
//                        	
//                        	/* 객체의 에러 발생 임계치 초과 여부 체크 */
//                        	if(ObjectContainer.contains(tmpHash)){
//
//                        		values = ObjectContainer.getAllValues(tmpHash); //해당 오브젝트의 값
//                        		
//                        		//임계시간 초과 시 메세지 추가 후 메일 전송
//                        		if(ObjectContainer.exceedLimitTime(tmpHash)){
//                        			
//                        			additionalMessage = String.format(
//                        					"[위 험] : 에러가 %d분 동안 지속 발생중입니다! (제한시간: %d분)\n"+
//                        					"          최초 발생시간: %s\n"+
//                        					"          누적 에러 발생 횟수: %d 회\n",
//                        					(curTime - values[ObjectContainer.ERR_START_TIME]) / A_MINUTE,
//                        					(values[ObjectContainer.ERR_LIMIT_TIME]) / A_MINUTE, 
//                        					sdf.format(new Date(values[ObjectContainer.ERR_START_TIME])),
//                        					values[ObjectContainer.ERR_COUNT]);
//                        			
//                        			/* 임계시간 초과 시 메일 보내고 객체 정보 초기화 -> 정책별로 상이 */
//                        			//ObjectContainer.remove(tmpHash);
//                        			ObjectContainer.setValue(tmpHash, ObjectContainer.ERR_START_TIME, curTime);
//                        		}
//                        		else{ 
//                        			//아직 임계시간 안지났으면 에러발생횟수 + 1 갱신,
//                        			//마지막 에러 발생시간 갱신 후 종료
//                        			ObjectContainer.setValue(tmpHash, ObjectContainer.ERR_COUNT, values[ObjectContainer.ERR_COUNT] + 1);
//                        			ObjectContainer.setValue(tmpHash, ObjectContainer.ERR_LAST_TIME, curTime);
//                        			return;
//                        		}
//                        	}
//                        	else{ //객체가 없다 == 첫 에러 발생
//                        		
//                        		//테스트용 임계시간 2분, 안정시간 3분
//                        		// 2분동안 계속 에러나면 메일보낸다
//                        		// 3분간 에러가 한번도 없으면 객체 삭제
//                        		ObjectContainer.init(tmpHash, curTime, 2*A_MINUTE, 3*A_MINUTE);
//                        		return; //첫 에러 발생에 대한 정보 등록 후 종료
//                        	}
//                        	/********************************************************************/
                        	
                        	
							// Get server configurations for email
							String hostname = conf.getValue("ext_plugin_email_smtp_hostname", "smtp.gmail.com"); // smtp 지정
							int port = conf.getInt("ext_plugin_email_smtp_port", 587); // smtp 포트번호
							
							String username = conf.getValue("ext_plugin_email_username", "haniumscouter@gmail.com"); // 발신자 이메일 계정
							String password = conf.getValue("ext_plugin_email_password", "dkqorhvk!@#$"); // 발신자 이메일 비밀번호
							
							boolean tlsEnabled = conf.getBoolean("ext_plugin_email_tls_enabled", true); // tls 여부(gamil은 true)
							
							String from = conf.getValue("ext_plugin_email_from_address", "haniumscouter@gmail.com"); // 수신자에게 표시될 이메일 정보
							String to = conf.getValue("ext_plugin_email_to_address", "occidere@naver.com"); // 수신자 이메일(공백없이 ,로 구분)
							String cc = conf.getValue("ext_plugin_email_cc_address"); // cc 이메일

							assert hostname != null;
							assert port > 0;
							assert username != null;
							assert password != null;
							assert from != null;
							assert to != null;
                        	
                        	// Get agent Name. 건들지 말자.
                        	String name = AgentManager.getAgentName(pack.objHash) == null ? "N/A" : AgentManager.getAgentName(pack.objHash);
                        	
							if (name.equals("N/A") && pack.message.endsWith("connected.")) {
								int idx = pack.message.indexOf("connected");
								if (pack.message.indexOf("reconnected") > -1)
									name = pack.message.substring(0, idx - 6);
								else
									name = pack.message.substring(0, idx - 4);
							}
                        	
                        	// Make email subject
                            String subject = "[" + AlertLevel.getName(pack.level) + "] " + pack.objType.toUpperCase() + 
                                          	 "(" + name + ") : " + pack.title;
                            
                            String title = pack.title;
                            String msg = pack.message;
                            
                            /* 
                             * Agent 객체가 죽으면(inactivate) 자체로 메일 보냄.
                             * 애초에 가비지 컬렉션, 요청 응답시간 등은 Agent가 죽어버리면 의미가 없기 때문에
                             * Agent 사망 시 title을 포함한 메세지 정보를 싹 바꿔버림.
                             */
                            if (title.equals("INACTIVE_OBJECT")) {
                            	title = name + " 가 비활성화(Inactivated) 되었습니다!";
                            	msg = pack.message.substring(0, pack.message.indexOf("OBJECT") - 1);
                            }
                            
                            //본문에 표시될 에러 발생 시간 포맷
                        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                            
                            // Make email message
                            String message = "[한이음 실시간 성능 모니터링 시스템 알림 ver.20170707]\n\n" +
                            				 "[제 목] : " + title + "\n" + 
                            				 "[시 간] : " + sdf.format(new Date(pack.time)) + "\n" +
                            				 "[종 류] : " + pack.objType.toUpperCase() + "\n" + 
                                          	 "[이 름] : " + name + "\n" + 
                                          	 "[수 준] : " + AlertLevel.getName(pack.level) + "\n" +
                                          	 "[내 용] : " + msg +"\r\n";
                                          
                            // Create an Email instance
                            Email email = new SimpleEmail();
                            
                            email.setHostName(hostname);
                            email.setSmtpPort(port);
                            email.setAuthenticator(new DefaultAuthenticator(username, password));
                            email.setStartTLSEnabled(tlsEnabled);
                            email.setFrom(from);
                            email.setSubject(subject);
                            email.setMsg(message);
                            
                            //수신자는 , 를 기준으로 분리
                            for (String addr : to.split(",")) {
                            	email.addTo(addr);
                            }
                            
                            //cc가 있으면 , 를 기준으로 분리
                            if (cc != null) {
                            	for (String addr : cc.split(",")) email.addCc(addr);
                            }
                            
                            // Send the email
                            email.send();
                            
                            println("Email sent to [" + to + "] successfully.");
                        } catch (Exception e) {
                        	println("[에 러] : " + e.getMessage());
                        	
                        	if (conf._trace) {
                                e.printStackTrace();
                            }
                        }
        			}
        		}.start();
            }
        }
    }
    
    //Agent가 활성화(activate)되면 메일을 보낸다.
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
	public void object(ObjectPack pack) {
    	if (pack.version != null && pack.version.length() > 0) {
			AlertPack ap = null;
			ObjectPack op = AgentManager.getAgent(pack.objHash);
	    	
			if (op == null && pack.wakeup == 0L) {
				// in case of new agent connected
				ap = new AlertPack();
				ap.level = AlertLevel.INFO;
				ap.objHash = pack.objHash;
				ap.title = "Object 활성화 감지!";
				ap.message = pack.objName + "가 연결되었습니다.";
				ap.time = System.currentTimeMillis();

				if (AgentManager.getAgent(pack.objHash) != null) {
					ap.objType = AgentManager.getAgent(pack.objHash).objType;
				} else {
					ap.objType = "scouter";
				}

				alert(ap);

			} else if (op.alive == false) {
				// in case of agent reconnected
				ap = new AlertPack();
				ap.level = AlertLevel.INFO;
				ap.objHash = pack.objHash;
				ap.title = "Object가 활성화되었습니다.";
				ap.message = pack.objName + "가 재연결되었습니다.";
				ap.time = System.currentTimeMillis();
				ap.objType = AgentManager.getAgent(pack.objHash).objType;

				alert(ap);
	    	}
			// inactive state can be handled in alert() method.
			// Agent의 비활성화(inactivate)시 메일을 보내는 기능은 alert() 메서드 내부에 직접 구현되어 있음.
    	}
	}
	
    
	//요청시간이 임계치를 넘으면 메일을 보낸다.
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_XLOG)
	public void xlog(XLogPack pack) {
		try {
			final int LIMIT = 2000; // 직접 만든 임계치. 응답시간 임계치는 4초가 적당하다.

			// 요청 경과시간이 여기서 정한 시간(ms)를 넘기면 메일을 보낸다.
			int elapsedThreshold = conf.getInt("ext_plugin_elapsed_time_threshold", LIMIT);

			if (elapsedThreshold != 0 && pack.elapsed > elapsedThreshold) {
				String serviceName = TextRD.getString(DateUtil.yyyymmdd(pack.endTime), TextTypes.SERVICE, pack.service); //요청 페이지 URL은 이거로 얻어올 수 있다

				AlertPack ap = new AlertPack();
    			
				ap.level = AlertLevel.WARN;
				ap.objHash = pack.objHash;
				ap.title = "요청 응답시간이 너무 느립니다!";
		        ap.message = "[" + AgentManager.getAgentName(pack.objHash) + "] " 
		        				+ pack.service + "(" + serviceName + ") "
		        				+ "에 대한 요청 응답시간이 " + pack.elapsed + "ms로, 임계치(" + LIMIT + "ms)를 초과하였습니다.";
				ap.time = System.currentTimeMillis();
				ap.objType = AgentManager.getAgent(pack.objHash).objType;

				long limitTime = conf.getLong("elapsed_limit_time", 60000); //1분
		        long safeTime = conf.getLong("elapsed_safe_time", 120000); //2분
		        if(ObjectContainer.sendAlert(ap, serviceName, limitTime, safeTime)) alert(ap);
			}

		} catch (Exception e) {
			Logger.printStackTrace(e);
		}
	}

	// 가비지 컬렉션 시간이 임계치를 초과하면 메일을 보낸다.
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_COUNTER)
	public void counter(PerfCounterPack pack) {
		String objName = pack.objName;
		int objHash = HashUtil.hash(objName);
		String objType = null;
		String objFamily = null;

		if (AgentManager.getAgent(objHash) != null) {
			objType = AgentManager.getAgent(objHash).objType;
		}

		if (objType != null) {
			objFamily = CounterManager.getInstance().getCounterEngine().getObjectType(objType).getFamily().getName();
		}
		
		try {
			// in case of objFamily is javaee
			if (CounterConstants.FAMILY_JAVAEE.equals(objFamily)) {
	        	// save javaee type's objHash
	        	if (!javaeeObjHashList.contains(objHash)) {
	        		javaeeObjHashList.add(objHash);
	        	}
	        	
	        	if (pack.timetype == TimeTypeEnum.REALTIME) {
	        		
	        		final int LIMIT = 100; //직접 만든 임계치. GC Time의 경우 100ms가 적당하다.
	        		
	        		//gc time이 여기서 정한 임계치(ms)를 넘기면 메일을 보낸다.
	        		long gcTimeThreshold = conf.getLong("ext_plugin_gc_time_threshold", LIMIT);
	        		long gcTime = pack.data.getLong(CounterConstants.JAVA_GC_TIME);

	        		if (gcTimeThreshold != 0 && gcTime > gcTimeThreshold) {
	        			AlertPack ap = new AlertPack();
	        			
	    		        ap.level = AlertLevel.WARN;
	    		        ap.objHash = objHash;
	    		        ap.title = "가비지 컬렉션에 너무 많은 시간이 걸립니다!";
	    		        ap.message = objName + "의 GC time이 " + gcTime + "ms로, 임계치("+LIMIT+"ms)를 초과하였습니다.";
	    		        ap.time = System.currentTimeMillis();
	    		        ap.objType = objType;

	    		        long limitTime = conf.getLong("gc_time_limit_time", 100);
	    		        long safeTime = conf.getLong("gc_time_safe_time", 200);
	    		        if(ObjectContainer.sendAlert(ap, objName, limitTime, safeTime)) alert(ap); //객체명을 일단 objName으로 해놓음
	        		}
	        	}
	    	}
        } catch (Exception e) {
			Logger.printStackTrace(e);
        }
    }

    private void println(Object o) {
        if (conf.getBoolean("ext_plugin_email_debug", false)) {
            Logger.println(o);
        }
    }
    
    
    ////////////////////////////////////////////////////////////////
    /*
    @ServerPlugin(PluginConstants.PLUGIN_SERVER_SUMMARY)
    public void summary(SummaryPack pack){
    	AlertPack ap = new AlertPack();
    	ap.objHash = pack.objHash;
    	ap.time = pack.time;
    	ap.message = pack.toString();
    	
    	alert(ap);
    }
    */
    ////////////////////////////////////////////////////////////////
}