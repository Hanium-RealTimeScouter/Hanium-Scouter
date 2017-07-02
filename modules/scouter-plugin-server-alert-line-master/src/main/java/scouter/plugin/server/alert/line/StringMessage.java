package scouter.plugin.server.alert.line;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 12. 20.
 */
public class StringMessage {
    private String type = "text";
    private String text;

    public StringMessage(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
