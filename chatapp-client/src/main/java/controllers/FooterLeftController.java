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

    private CreateChatController createChatController;
    private CreateChatGroupController createChatGroupController;

    private CreateChat chatDialog;
    private CreateGroupChat chatGroupDialog;

    @Inject
    public FooterLeftController(FooterPanel footerPanel, CreateChatController createChatController, CreateChatGroupController createChatGroupController) {
        this.footerPanel = footerPanel;
        this.createChatController = createChatController;
        this.createChatGroupController = createChatGroupController;

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
