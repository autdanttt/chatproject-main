package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatItem {
    private Long chatId;
    private Long otherUserId;
    private String otherUsername;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String avatar;

    public ChatItem(Long chatId,Long otherUserId, String username, String lastMessage, LocalDateTime lastMessageTime) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getChatId() { return chatId; }
    public String getUsername() { return otherUsername; }
    public String getLastMessage() { return lastMessage; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }

    public String getOtherUsername() {
        return otherUsername;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public String getFormattedTime() {
        return lastMessageTime != null ? lastMessageTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}