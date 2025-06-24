package controllers;

import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.leftPanel.components.FooterPanel;
import view.main.rightPanel.message.MessageController;

import javax.inject.Inject;

public class FooterLeftController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
    private FooterPanel footerPanel;
    @Inject
    public FooterLeftController(FooterPanel footerPanel) {
        this.footerPanel = footerPanel;

        initializeListeners();
    }

    private void initializeListeners() {
        footerPanel.addChatListener(e -> {
            LOGGER.info("->>>>>>>>>>>>>>>>>>>>>>>> ");
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
