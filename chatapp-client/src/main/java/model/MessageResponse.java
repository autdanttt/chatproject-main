package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class MessageResponse {
    @JsonProperty("message_id")
    private Long messageId;      // Thêm để khớp với JSON
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_user_id")
    private Long toUserId;       // Thêm nếu cần (dùng để xác định chat 1-1)
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("message_type")
    private String messageType;  // Thêm nếu cần xử lý loại tin nhắn
    @JsonProperty("content")
    private String content;
    @JsonProperty("sent_at")
    private Date sentAt;
    @JsonProperty("delivered_at")
    private Date deliveredAt;

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "content='" + content + '\'' +
                ", messageId=" + messageId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                '}';
    }
}