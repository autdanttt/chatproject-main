package view.main.leftPanel.chatlist;

import model.ChatResponse;

import java.util.List;

public interface ChatListService {
    public ChatResponse[] getChatList(String jwtToken);
}
