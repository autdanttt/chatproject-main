package model;

public class MessageRequest {
    private Long fromUserId;
    private Long toUserId;
    private MessageType messageType;
    private String content;

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                '}';
    }
}
