package model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ChatItem {
    private Long chatId;
    private Long otherUserId;
    private String otherUsername;
    private String lastMessage;
    private Date lastMessageTime;


    public ChatItem(Long chatId, Long otherUserId, String username, String lastMessage, Date lastMessageTime) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = username;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getChatId() { return chatId; }
    public String getUsername() { return otherUsername; }
    public String getLastMessage() { return lastMessage; }
    public Date getLastMessageTime() { return lastMessageTime; }

    public String getOtherUsername() {
        return otherUsername;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public String getFormattedTime() {
        return lastMessageTime != null
                ? new SimpleDateFormat("HH:mm").format(lastMessageTime)
                : "";

    }
}