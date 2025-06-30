package view.main.leftPanel.chatlist;

public class ChatSelectedEvent {
    private Long chatId;
    private Long userId;
    private String type;

    public ChatSelectedEvent(Long chatId, Long userId, String type) {
        this.chatId = chatId;
        this.userId = userId;
        this.type = type;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}
