package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Date;

public class ChatResponse {

    private Long chatId;

    private Long otherUserId;

    private String otherUsername;

    private String lastMessage;

    private Date lastMessageTime;

    public ChatResponse() {
    }

    public ChatResponse(Long chatId, Long otherUserId, String otherUser, String lastMessage, Date lastMessageTime) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUser;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getChatId() { return chatId; }
    public String getOtherUsername() { return otherUsername; }
    public String getLastMessage() { return lastMessage; }
    public Date getLastMessageTime() { return lastMessageTime; }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "chatId=" + chatId +
                ", otherUsername='" + otherUsername + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}