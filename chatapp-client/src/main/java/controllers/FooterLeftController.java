package controllers;

import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.leftPanel.components.FooterPanel;
import view.main.dialog.CreateChat;
import view.main.dialog.CreateGroupChat;
import view.main.rightPanel.message.MessageController;

import javax.inject.Inject;
import javax.swing.*;

public class FooterLeftController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private FooterPanel footerPanel;
    private CreateGroupChat createGroupChat;
    private CreateChatController createChatController;
    private CreateChat chatDialog;

    @Inject
    public FooterLeftController(FooterPanel footerPanel, CreateGroupChat createGroupChat, CreateChatController createChatController) {
        this.footerPanel = footerPanel;
        this.createGroupChat = createGroupChat;
        this.createChatController = createChatController;

        initializeListeners();
    }

    private void initializeListeners() {
        footerPanel.addChatListener(e -> {
            chatDialog = new CreateChat((JFrame) SwingUtilities.getWindowAncestor(footerPanel));
            createChatController.setCreateChat(chatDialog);
            createChatController.showDialogAndLoadUserList();
            chatDialog.dispose();
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
