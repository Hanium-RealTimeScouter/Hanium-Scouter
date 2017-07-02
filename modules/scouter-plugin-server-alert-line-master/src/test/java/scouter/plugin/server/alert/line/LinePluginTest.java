package scouter.plugin.server.alert.line;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scouter.lang.AlertLevel;
import scouter.lang.pack.AlertPack;
import scouter.server.Configure;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinePluginTest {
    public final static String lineAccessToken = "Wd8jVkD5Fzh7CMl1CTmyOo9ILtWq1MoknQ7kbTMMjQmdU6+cDmfqkwwuE5mB5rLQcFeWCjvjJgnE/MmqT6D+gEsO68vKQh11YygUT7dQmh1JwmWG5mbRqk98Xo1+aBWHllG0AL/6xAp7YMtG9MDVPwdB04t89/1O/w1cDnyilFU=";
    public static final String lineGroupId = "C0246cfa665d99ec6dde3f12bec77eb4a";

    @Mock Configure conf;
    @InjectMocks LinePlugin plugin = new LinePlugin();

    @Test
    public void alert() throws Exception {
        when(conf.getValue("ext_plugin_line_access_token")).thenReturn(lineAccessToken);
        when(conf.getValue("ext_plugin_line_group_id")).thenReturn(lineGroupId);
        when(conf.getBoolean(eq("ext_plugin_line_send_alert"), anyBoolean())).thenReturn(true);

        AlertPack ap = genAlertPack();
        plugin.alert(ap);

        Thread.sleep(10000);
    }

    private static AlertPack genAlertPack() {
        AlertPack ap = new AlertPack();

        ap.level = AlertLevel.WARN;
        ap.objHash = 100;
        ap.title = "Elapsed time exceed a threshold.";
        ap.message = "[100 agent] exceed a threshold";
        ap.time = System.currentTimeMillis();
        ap.objType = "someObjecttype";

        return ap;
    }

}
