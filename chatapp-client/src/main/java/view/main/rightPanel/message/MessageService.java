package view.main.rightPanel.message;

import model.MessageResponse;

public interface MessageService {
    public MessageResponse[] getMessageByChatId(Long chatId, String jwtToken);
}
