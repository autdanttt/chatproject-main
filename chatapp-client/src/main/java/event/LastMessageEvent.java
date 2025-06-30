package event;


public class LastMessageEvent {
    private Long lastVisibleId;
    public LastMessageEvent(long lastVisibleId) {
        this.lastVisibleId = lastVisibleId;
    }
    public Long getLastVisibleId() {
        return lastVisibleId;
    }
}
