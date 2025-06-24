package view.main.leftPanel.chatlist;

public class ChatSelectedEvent {
    private Long chatId;
    private Long userId;

    public ChatSelectedEvent(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }

}
