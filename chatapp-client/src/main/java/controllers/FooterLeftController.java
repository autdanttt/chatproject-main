package controllers;

import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.leftPanel.components.FooterPanel;
import view.main.popup.CreateChat;
import view.main.popup.CreateGroupChat;
import view.main.rightPanel.message.MessageController;

import javax.inject.Inject;

public class FooterLeftController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
    private FooterPanel footerPanel;
    private CreateGroupChat createGroupChat;
    private CreateChat createChat;
    @Inject
    public FooterLeftController(FooterPanel footerPanel, CreateGroupChat createGroupChat, CreateChat createChat) {
        this.footerPanel = footerPanel;
        this.createGroupChat = createGroupChat;
        this.createChat = createChat;

        initializeListeners();
    }

    private void initializeListeners() {
        footerPanel.addChatListener(e -> {
           createChat.setVisible(true);
        });

        footerPanel.deleteChatListener(e -> {
            LOGGER.info("->>>>>>>>>>>>>>>>>>>>>>>>>> dffdsfsd");
        });

        footerPanel.addGroupListener(e -> {
            createGroupChat.setVisible(true);
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
