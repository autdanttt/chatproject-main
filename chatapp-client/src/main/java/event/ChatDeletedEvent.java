package event;

public class ChatDeletedEvent {
    private final Long chatId;

    public ChatDeletedEvent(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
}
