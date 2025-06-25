package view.main.leftPanel.chatlist;

import model.ChatGroupResponse;
import model.ChatResponse;

import java.util.List;

public interface ChatListService {
    public ChatResponse[] getChatList(String jwtToken);

    public ChatGroupResponse[] getChatGroupList(String jwtToken);
}
