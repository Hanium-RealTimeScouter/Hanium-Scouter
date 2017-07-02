package scouter.plugin.server.alert.line;

import com.google.gson.Gson;
import org.junit.Test;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 */
public class LinePushFormatTest {
    @Test
    public void checkToJson() throws Exception {
        LinePushFormat format = new LinePushFormat();
        format.setTo("ttttttttt");
        StringMessage[] msgs = new StringMessage[1];
        format.addMessage(new StringMessage("It's my message"));
        String param = new Gson().toJson(format);

        System.out.println("================");
        System.out.println(param);

    }

}
