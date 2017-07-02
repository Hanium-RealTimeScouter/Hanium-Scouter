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
package scouter.plugin.server.alert.line;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import scouter.lang.AlertLevel;
import scouter.lang.TimeTypeEnum;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.pack.PerfCounterPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.server.Configure;
import scouter.server.CounterManager;
import scouter.server.Logger;
import scouter.server.core.AgentManager;
import scouter.util.HashUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Scouter server plugin to send alert via line
 *
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 * @author original Sang-Cheon Park(nices96@gmail.com)
 */
public class LinePlugin {

	// Get singleton Configure instance from server
    Configure conf = Configure.getInstance();

    private static List<Integer> javaeeObjHashList = new ArrayList<Integer>();

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
    public void alert(final AlertPack pack) {
        if (conf.getBoolean("ext_plugin_line_send_alert", true)) {
        	
        	// Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
        	int level = conf.getInt("ext_plugin_line_level", 0);
        	
        	if (level <= pack.level) {
        		new Thread() {
        			public void run() {
                        try {
                        	
                        	final String TOKEN = "mUtsVzBCI/OAw+Teh/bqhkhKOQqDBzHj1V3JZp4YvD90AHPJZyOqQe1P1Ju9U2TiQ0hDcU7Dtl5y/ntZClo34mPJvthdQ6+DBwWB/jun4Azz7lDGbDQvCA8WyDd8COjNWjw4UCHOOcOus8+A0kapDAdB04t89/1O/w1cDnyilFU=";
                        	final String CHATID = "C775723dfb27b1b73e06ccafade07ae03";
                        	
                        	// Get server configurations for line\
                            String token = conf.getValue("ext_plugin_line_access_token", TOKEN);
                            String chatId = conf.getValue("ext_plugin_line_group_id", CHATID);
                            
                            assert token != null;
                            assert chatId != null;
                        
                            // Make a request URL using telegram bot api
                            String url = "https://api.line.me/v2/bot/message/push";

                        	// Get the agent Name
                        	String name = AgentManager.getAgentName(pack.objHash) == null ? "N/A" : AgentManager.getAgentName(pack.objHash);
                        	
                        	if (name.equals("N/A") && pack.message.endsWith("connected.")) {
                    			int idx = pack.message.indexOf("connected");
                        		if (pack.message.indexOf("reconnected") > -1) {
                        			name = pack.message.substring(0, idx - 6);
                        		} else {
                        			name = pack.message.substring(0, idx - 4);
                        		}
                        	}
                            
                            String title = pack.title;
                            String msg = pack.message;
                            if (title.equals("INACTIVE_OBJECT")) {
                            	title = "An object has been inactivated.";
                            	msg = pack.message.substring(0, pack.message.indexOf("OBJECT") - 1);
                            }
                          
                        	// Make message contents
                            String contents = "[TYPE] : " + pack.objType.toUpperCase() + "\n" + 
                                           	  "[NAME] : " + name + "\n" + 
                                              "[LEVEL] : " + AlertLevel.getName(pack.level) + "\n" +
                                              "[TITLE] : " + title + "\n" + 
                                              "[MESSAGE] : " + msg;

                            LinePushFormat pushFormat = new LinePushFormat();
                            pushFormat.setTo(chatId);
                            pushFormat.addMessage(new StringMessage(contents));

                            String body = new Gson().toJson(pushFormat);
                  
                            HttpPost post = new HttpPost(url);
                            post.addHeader("Content-Type","application/json");
                            post.addHeader("Authorization", "Bearer {" + token + "}");
                            post.setEntity(new StringEntity(body));
                          
                            CloseableHttpClient client = HttpClientBuilder.create().build();
                          
                            // send the post request
                            HttpResponse response = client.execute(post);
                            
                            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                println("Line message sent to [" + chatId + "] successfully.");
                            } else {
                                println("Line message sent failed. Verify below information.");
                                println("[URL] : " + url);
                                println("[Message] : " + body);
                                println("[Reason] : " + EntityUtils.toString(response.getEntity(), "UTF-8"));
                            }
                        } catch (Exception e) {
                        	println("[Error] : " + e.getMessage());
                        	
                        	if(conf._trace) {
                                e.printStackTrace();
                            }
                        }
        			}
        		}.start();
            }
        }
    }
    
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
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is connected.";
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
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is reconnected.";
		        ap.time = System.currentTimeMillis();
		        ap.objType = AgentManager.getAgent(pack.objHash).objType;
				
		        alert(ap);
	    	}
			// inactive state can be handled in alert() method.
    	}
	}

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
	        		long gcTimeThreshold = conf.getLong("ext_plugin_gc_time_threshold", 0);
	        		long gcTime = pack.data.getLong(CounterConstants.JAVA_GC_TIME);

	        		if (gcTimeThreshold != 0 && gcTime > gcTimeThreshold) {
	        			AlertPack ap = new AlertPack();
	        			
	    		        ap.level = AlertLevel.WARN;
	    		        ap.objHash = objHash;
	    		        ap.title = "GC time exceed a threshold.";
	    		        ap.message = objName + "'s GC time(" + gcTime + " ms) exceed a threshold.";
	    		        ap.time = System.currentTimeMillis();
	    		        ap.objType = objType;
	    				
	    		        alert(ap);
	        		}
	        	}
	    	}
        } catch (Exception e) {
			Logger.printStackTrace(e);
        }
    }

    private void println(Object o) {
        if (conf.getBoolean("ext_plugin_line_debug", false)) {
            Logger.println(o);
        }
    }
}
