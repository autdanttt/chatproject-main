package view.main.leftPanel.chatlist;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.*;
import model.ChatGroupResponse;
import model.ChatItem;
import model.ChatResponse;
import model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import view.login.TokenManager;
import view.main.UserToken;

import javax.swing.*;
import java.util.*;

public class ChatListController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ChatListController.class);
    private final ChatListService chatListService;
    private final ChatListPanel chatListPanel;
    private EventBus eventBus;

    Map<Long, ChatItem> privateChatCache = new HashMap<>(); // key: chatId
    Map<Long, ChatItem> groupChatCache = new HashMap<>(); // key: groupId

    int selectedIndex = -1;

    private Long currentPrivateChatId = null;
    private Long currentGroupId = null;

    @Inject
    public ChatListController(ChatListPanel chatListPanel, ChatListService chatListService, EventBus eventBus) {
        this.chatListPanel = chatListPanel;
        this.chatListService = chatListService;
        this.eventBus = eventBus;
        eventBus.register(this);
        initializeListeners();
    }

    private void initializeListeners() {
        chatListPanel.getChatList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ChatItem selectedChat = chatListPanel.getChatList().getSelectedValue();
                selectedIndex = chatListPanel.getChatList().getSelectedIndex();
                if (selectedChat != null) {
                    if (selectedChat.getOtherUserId() == null) {
                        // Là group
                        currentPrivateChatId = null;
                        currentGroupId = selectedChat.getChatId(); // vì bạn dùng chatId cho cả group
                    } else {
                        // Là 1-1
                        currentGroupId = null;
                        currentPrivateChatId = selectedChat.getChatId();
                    }
                }
                if (selectedChat != null) {
                    String type = selectedChat.getOtherUserId() == null ? "GROUP" : "CHAT";
                    eventBus.post(new ChatSelectedEvent(selectedChat.getChatId(), selectedChat.getOtherUserId(), type));
                    eventBus.post(new FullNameUpdateEvent(selectedChat.getOtherUserFullName(), selectedChat.getAvatarUrl()));
                    logger.info("nameeeeeeeee: "+ selectedChat.getOtherUserFullName());
                }
            }
        });
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        reloadChatList();
    }

    @Subscribe
    public void OnChatDelete(ChatDeletedEvent chatDeletedEvent) {
        reloadChatList();
    }

    @Subscribe
    public void onCreateChat(ChatCreatedEvent chatCreatedEvent) {
        reloadChatList();
    }
    @Subscribe
    public void onGroupRenamed(GroupRenamedEvent groupRenamedEvent) {
        reloadChatList();
    }
    @Subscribe
    public void onRenamed(FullNameUpdateEvent fullNameUpdateEvent) {
        reloadChatList();
    }


    public void reloadChatList() {
        List<ChatItem> combinedList = new ArrayList<>();

        // Tạo danh sách ChatItem từ ChatResponse
        ChatResponse[] list = getListChat();
        for (ChatResponse chat : list) {
            ChatItem chatItem = new ChatItem(
                    chat.getChatId(),
                    chat.getOtherUserId(),
                    chat.getOtherUserFullName(),
                    chat.getLastMessage(),
                    chat.getLastMessageTime(),
                    chat.getImageUrl()
            );
            combinedList.add(chatItem);
            privateChatCache.put(chat.getChatId(),chatItem);
        }

        // Tạo danh sách ChatItem từ ChatGroupResponse
        ChatGroupResponse[] listGroup = chatListService.getChatGroupList(TokenManager.getAccessToken());
        for (ChatGroupResponse group : listGroup) {
            ChatItem chatItem = new ChatItem(
                    group.getGroupId(),
                    null,
                    group.getGroupName(),
                    group.getLastMessageContent(),
                    group.getLastMessageTime(),
                    group.getImageUrl()
            );
            combinedList.add(chatItem);
            groupChatCache.put(group.getGroupId(),chatItem);
        }

        // Sắp xếp theo thời gian giảm dần
        combinedList.sort((a, b) -> {
            Date timeA = a.getLastMessageTime();
            Date timeB = b.getLastMessageTime();
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA); // mới nhất trước
        });

        // Đưa vào model
        chatListPanel.getChatListModel().clear();
        for (ChatItem item : combinedList) {
            chatListPanel.getChatListModel().addElement(item);
        }
    }
    @Subscribe
    public void addMessage(MessageResponse response) {
        updateChatListOnNewMessage(response);
    }

    public void updateChatListOnNewMessage(MessageResponse message) {
        boolean isGroup = message.getGroupId() != null;
        Long key = isGroup ? message.getGroupId() : message.getChatId();

        JList<ChatItem> chatList = chatListPanel.getChatList();
        DefaultListModel<ChatItem> model = chatListPanel.getChatListModel();

        // Lưu lại ID của chat hiện đang được chọn
        Long selectedChatId = null;
        boolean selectedIsGroup = false;

        ChatItem selectedItem = chatList.getSelectedValue();
        if (selectedItem != null) {
            selectedChatId = selectedItem.getChatId();
            selectedIsGroup = (selectedItem.getOtherUserId() == null);
        }

        // Tìm và cập nhật hoặc tạo ChatItem mới
        for (int i = 0; i < model.size(); i++) {
            ChatItem item = model.get(i);

            boolean isSameType = (item.getOtherUserId() == null) == isGroup;
            if (Objects.equals(item.getChatId(), key) && isSameType) {
                item.setLastMessage(message.getContent());
                item.setLastMessageTime(message.getSentAt());

                model.remove(i);
                model.add(0, item);
                break;
            }
        }

        boolean alreadyExists = false;
        for (int i = 0; i < model.size(); i++) {
            ChatItem item = model.get(i);
            boolean isSameType = (item.getOtherUserId() == null) == isGroup;
            if (Objects.equals(item.getChatId(), key) && isSameType) {
                alreadyExists = true;
                break;
            }
        }


        if (!alreadyExists) {
            ChatItem cached = isGroup ? groupChatCache.get(key) : privateChatCache.get(key);
            ChatItem newItem = cached != null
                    ? ChatItem.builder()
                    .chatId(cached.getChatId())
                    .otherUserId(cached.getOtherUserId())
                    .otherUserFullName(cached.getOtherUserFullName())
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getSentAt())
                    .avatarUrl(cached.getAvatarUrl())
                    .build()
                    : ChatItem.builder()
                    .chatId(key)
                    .otherUserId(isGroup ? null : message.getFromUserId())
                    .otherUserFullName(message.getFromFullName())
                    .lastMessage(message.getContent())
                    .lastMessageTime(message.getSentAt())
                    .avatarUrl(null)
                    .build();

            model.add(0, newItem);
        }

        //  Restore lại selection nếu chat đang mở không phải cái vừa update
        if (selectedChatId != null) {
            for (int i = 0; i < model.size(); i++) {
                ChatItem item = model.get(i);
                boolean isSameType = (item.getOtherUserId() == null) == selectedIsGroup;
                if (Objects.equals(item.getChatId(), selectedChatId) && isSameType) {
                    chatList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        chatListPanel.getChatList().clearSelection();
    };

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
