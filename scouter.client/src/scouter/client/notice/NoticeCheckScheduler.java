package scouter.client.notice;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.ui.PlatformUI;

import scouter.client.util.ExUtil;
import scouter.client.util.RCPUtil;
import scouter.util.DateUtil;
import scouter.util.FileUtil;
import scouter.util.StringUtil;
import scouter.util.ThreadUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public enum NoticeCheckScheduler {

    INSTANCE;

    private final String NOTICE_URL = "http://notice.scouterapm.com:6181/latest-notice";
    private final String NOTICE_FILENAME = "notice_tag";
    String scch = Long.toHexString(Double.doubleToLongBits(Math.random()));
    boolean initialized = false;
    
    public void initialize() {
        ExUtil.asyncRun(() -> {
	        	if (!initialized) {
	        		initialized = true;
	        		try {
	        			ThreadUtil.sleep(5000);
		            process();
		            registerTimer();
	        		} catch (Exception e) {
	        			initialized = false;
	        			e.printStackTrace();
	        		}
	        	}
        });
    }

    void registerTimer() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Date checkTime = calendar.getTime();
        if (System.currentTimeMillis() > checkTime.getTime()) {
        		calendar.add(Calendar.DATE, 1);
        		checkTime = calendar.getTime();
        }
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
	            	try {
	            		System.out.println("Timer Start");
	                process();
	            	} catch (Exception e) { }
            }
        }, checkTime, DateUtil.MILLIS_PER_DAY);
    }

    private void process() throws ClientProtocolException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(NOTICE_URL);
        httpGet.addHeader("X-SCCH", scch);
        HttpResponse response = null;
        response = httpClient.execute(httpGet);
        System.out.println("Notice Response Code : "
                + response.getStatusLine().getStatusCode());
        String cacheTag = response.getFirstHeader("X-Scouter-ETag").getValue();
        if (StringUtil.isEmpty(cacheTag)) return;
        File noticeFile = new File(RCPUtil.getWorkingDirectory(), NOTICE_FILENAME);
        if (noticeFile.exists() == false // first time 
        		|| cacheTag.equals(new String(FileUtil.readAll(noticeFile), "UTF-8")) == false // changed notice
        		|| noticeFile.lastModified() < (System.currentTimeMillis() - DateUtil.MILLIS_PER_DAY * 30)) { // a month ago noticed
        		HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, "UTF-8");
            ExUtil.exec(PlatformUI.getWorkbench().getDisplay(), new Runnable() {
				@Override
				public void run() {
					System.out.println("Open Notice Dialog");
					new NoticeDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), html).open();
				}
            });
            FileUtil.save(noticeFile, cacheTag.getBytes());
        }
    }
}
