package view.main.leftPanel.chatlist;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.FullNameUpdateEvent;
import model.ChatGroupResponse;
import model.ChatItem;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import view.login.TokenManager;
import view.main.UserToken;

public class ChatListController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ChatListController.class);
    private final ChatListService chatListService;
    private final ChatListPanel chatListPanel;

    @Inject
    public ChatListController(ChatListPanel chatListPanel, ChatListService chatListService, EventBus eventBus) {
        this.chatListPanel = chatListPanel;
        this.chatListService = chatListService;
        eventBus.register(this);
        initializeListeners();
    }

    private void initializeListeners() {
        chatListPanel.getChatList().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                ChatItem selectedChat = chatListPanel.getChatList().getSelectedValue();
                if(selectedChat != null) {
                    String type = selectedChat.getOtherUserId() == null ? "GROUP": "CHAT";
                    eventBus.post(new ChatSelectedEvent(selectedChat.getChatId(), selectedChat.getOtherUserId(), type));
                    eventBus.post(new FullNameUpdateEvent(selectedChat.getOtherUserFullName(), selectedChat.getAvatarUrl()));
                }
            }
        });
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        ChatResponse[] list = getListChat();
        chatListPanel.getChatListModel().clear();

        for (ChatResponse chat : list) {
            chatListPanel.getChatListModel().addElement(new ChatItem(
                    chat.getChatId(),
                    chat.getOtherUserId(),
                    chat.getOtherUserFullName(),
                    chat.getLastMessage(),
                    chat.getLastMessageTime(),
                    chat.getImageUrl()
            ));
        }

        ChatGroupResponse[] listGroup = chatListService.getChatGroupList(TokenManager.getAccessToken());
        for(ChatGroupResponse group : listGroup) {
            chatListPanel.getChatListModel().addElement(new ChatItem(
                    group.getGroupId(),
                    null,
                    group.getGroupName(),
                    group.getLastMessageContent(),
                    group.getLastMessageTime(),
                    group.getImageUrl()
            ));
        }
    }

    private ChatResponse[] getListChat() {
        return chatListService.getChatList(TokenManager.getAccessToken());
    }

    @Override
    protected void setupDependencies() {

    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
