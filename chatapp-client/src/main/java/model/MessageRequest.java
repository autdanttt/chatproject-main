package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRequest {
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_user_id")
    private Long toUserId;
    @JsonProperty("message_type")
    private MessageType messageType;
    @JsonProperty("content")
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
