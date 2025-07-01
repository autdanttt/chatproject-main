package controllers;

import api.ChatApi;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import di.BaseController;
import event.ChatDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.leftPanel.chatlist.ChatSelectedEvent;
import view.main.leftPanel.components.FooterPanel;
import view.main.dialog.CreateChat;
import view.main.dialog.CreateGroupChat;

import javax.inject.Inject;
import javax.swing.*;

public class FooterLeftController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(FooterLeftController.class);
    private final FooterPanel footerPanel;

    private final CreateChatController createChatController;

    private final CreateChatGroupController createChatGroupController;

    private CreateChat chatDialog;
    private CreateGroupChat chatGroupDialog;
    private ChatApi chatApi;
    private Long chatIdBoth;
    private String type;

    @Inject
    public FooterLeftController(FooterPanel footerPanel, CreateChatController createChatController, CreateChatGroupController createChatGroupController, ChatApi chatApi, EventBus eventBus) {
        this.footerPanel = footerPanel;
        this.createChatController = createChatController;
        this.createChatGroupController = createChatGroupController;
        this.chatApi = chatApi;
        eventBus.register(this);

        initializeListeners();
    }

    private void initializeListeners() {
        footerPanel.addChatListener(e -> {
            chatDialog = new CreateChat((JFrame) SwingUtilities.getWindowAncestor(footerPanel));
            createChatController.setCreateChat(chatDialog);
            createChatController.showDialogAndLoadUserList();
            chatDialog.dispose();
        });

        footerPanel.addGroupListener(e -> {
            chatGroupDialog = new CreateGroupChat((JFrame) SwingUtilities.getWindowAncestor(footerPanel));
            createChatGroupController.setCreateGroupChat(chatGroupDialog);
            createChatGroupController.showDialogAndLoadUserList();
            chatGroupDialog.dispose();
        });

        footerPanel.deleteChatListener(e -> {
            logger.info(">>>>>>>>>>>>>Chat deleted: {} - {}", chatIdBoth, type);
            switch (type) {
                case "CHAT":
                    boolean successDeleteChat = chatApi.deleteChat(chatIdBoth);
                    eventBus.post(new ChatDeletedEvent(chatIdBoth));
                    if (successDeleteChat) {
                        logger.info(">>>>>>>>>>>>>>>>>>>>>Delete Chat Success<<<<<<<<<<<<<<<<<<<<<<<");
                    } else {
                        logger.error(">>>>>>>>>>>>>>>>>>>>>Delete Chat Failed<<<<<<<<<<<<<<<<<<<<<");
                    }
                    break;
                case "GROUP":
                    boolean successDeleteChatGroup = chatApi.deleteGroupChat(chatIdBoth);
                    eventBus.post(new ChatDeletedEvent(chatIdBoth));
                    if (successDeleteChatGroup) {
                        logger.info(">>>>>>>>>>>>>>>>>>>>>>>Delete Group Chat Success<<<<<<<<<<<<<<<<<<<<<");
                    }else {
                        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>Delete Group Chat Failed<<<<<<<<<<<<<<<<<<<<<");
                    }
                    break;
            }
        });
    }

    @Subscribe
    public void addChatIdUserAndGroup(ChatSelectedEvent event) {
        chatIdBoth = event.getChatId();
        type = event.getType();
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
