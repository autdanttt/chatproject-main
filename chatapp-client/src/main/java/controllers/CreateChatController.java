package controllers;

import api.ChatApi;
import api.UserApi;
import com.google.common.eventbus.EventBus;
import di.BaseController;
import event.ChatCreatedEvent;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.TokenManager;
import view.main.dialog.CreateChat;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class CreateChatController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CreateChatController.class);

    private final UserApi userApi;
    private final ChatApi chatApi;
    private CreateChat createChat;
    private Long id;

    @Inject
    public CreateChatController(UserApi userApi, ChatApi chatApi, EventBus eventBus) {
        this.userApi = userApi;
        this.chatApi = chatApi;
        this.eventBus = eventBus;
    }

    public void setCreateChat(CreateChat createChat) {
        this.createChat = createChat;
        initializeListeners();
    }

    private void initializeListeners() {
        createChat.handlerAddChat(e -> handlerAddChatUser());
        createChat.selectItemListUser(e -> {
            if (!e.getValueIsAdjusting()) {
                User selected = createChat.getUserList().getSelectedValue();
                id = selected.getId();
            }
        });
    }

    private void handlerAddChatUser() {
        if (id == null) {
            JOptionPane.showMessageDialog(null, "Please select a user");
        }
        try {
            chatApi.createChat(id);
            eventBus.post(new ChatCreatedEvent());
            createChat.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to create chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showDialogAndLoadUserList() {
        createChat.getUserListModel().clear();

        try {
            List<User> users = userApi.getAllOtherUsers(
                    TokenManager.getUsername(),
                    TokenManager.getAccessToken()
            );

            for (User user : users) {
                logger.info("Other id: " + user.getId());
                createChat.getUserListModel().addElement(user);
            }

            createChat.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
