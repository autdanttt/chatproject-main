package view.main.chatlist.chatlist;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.UsernameUpdateEvent;
import model.ChatItem;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import view.main.UserToken;

public class ChatListController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatListController.class);
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
//                    eventBus.post(selectedChat.getChatId());
                    eventBus.post(new ChatSelectedEvent(selectedChat.getChatId(), selectedChat.getOtherUserId()));
                    eventBus.post(new UsernameUpdateEvent(selectedChat.getUsername()));
                }
            }
        });

    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        LOGGER.info("Received JWT token: " + userToken.getJwtToken());

        ChatResponse[] list = getListChat(userToken.getJwtToken());

        chatListPanel.getChatListModel().clear();

        for (ChatResponse chat : list) {
            LOGGER.info("Adding chat: " + chat);
            chatListPanel.getChatListModel().addElement(new ChatItem(
                    chat.getChatId(),
                    chat.getOtherUserId(),
                    chat.getOtherUsername(),
                    chat.getLastMessage(),
                    chat.getLastMessageTime()
            ));
        }

    }

    private ChatResponse[] getListChat(String jwtToken) {
        return chatListService.getChatList(jwtToken);
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
