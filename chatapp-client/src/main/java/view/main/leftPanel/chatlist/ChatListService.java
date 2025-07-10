package view.main.leftPanel.chatlist;

import model.ChatGroupResponse;
import model.ChatResponse;

public interface ChatListService {
    public ChatResponse[] getChatList(String jwtToken);

    public ChatGroupResponse[] getChatGroupList(String jwtToken);
}
