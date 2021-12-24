package byron.dlna;

public class NativeAsyncEvent {
    public String url;
    public String type;
    public String title;

    public NativeAsyncEvent(String type, String url, String title) {
        this.url = url;
        this.type = type;
        this.title = title;
    }
}
