package controllers;

import api.ChatApi;
import api.UserApi;
import com.google.common.eventbus.EventBus;
import di.BaseController;
import event.ChatCreatedEvent;
import model.UserOther;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.TokenManager;
import view.main.dialog.CreateGroupChat;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateChatGroupController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CreateChatController.class);
    private Long currentUserId;
    private CreateGroupChat createGroupChatChat;
    private UserApi userApi;
    private ChatApi chatApi;

    @Inject
    public CreateChatGroupController(UserApi userApi, ChatApi chatApi, EventBus eventBus) {
        this.userApi = userApi;
        this.chatApi = chatApi;
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public void setCreateGroupChat(CreateGroupChat createGroupChatChat) {
        this.createGroupChatChat = createGroupChatChat;

        initializeListeners();
    }

    public void showDialogAndLoadUserList() {
        createGroupChatChat.getUserListModel().clear();

        try {
            List<UserOther> users = userApi.getAllOtherUsers(TokenManager.getAccessToken());

            for (UserOther user : users) {
                createGroupChatChat.getUserListModel().addElement(user);
            }

            createGroupChatChat.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeListeners() {
        createGroupChatChat.addBtnGroupListener(e -> {
            String name = createGroupChatChat.getNameGrouptxt().getText().trim();

            currentUserId = TokenManager.getUserId();
            List<UserOther> selectedUsers = createGroupChatChat.getUserList().getSelectedValuesList();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(createGroupChatChat,
                        "Vui lòng nhập tên nhóm trước khi tạo.",
                        "Thiếu tên nhóm",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedUsers.isEmpty()) {
                JOptionPane.showMessageDialog(createGroupChatChat,
                        "Vui lòng chọn ít nhất một thành viên để tạo nhóm.",
                        "Chưa chọn thành viên",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Long> memberIds = new ArrayList<>();
            memberIds.add(currentUserId);
            for (UserOther user : selectedUsers) {
                memberIds.add(user.getId());
            }

            chatApi.createGroupChat(name, currentUserId, memberIds, null);

            eventBus.post(new ChatCreatedEvent());
            createGroupChatChat.dispose();
        });
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
