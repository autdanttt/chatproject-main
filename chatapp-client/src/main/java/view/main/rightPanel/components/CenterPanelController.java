package view.main.rightPanel.components;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import di.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.leftPanel.chatlist.ChatSelectedEvent;
import view.main.rightPanel.RightPanel;

import javax.inject.Inject;
import javax.inject.Provider;


public class CenterPanelController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CenterPanelController.class);
    private CenterRightPanel centerRightPanel;
    private Provider<RightPanel> rightPanelProvider;

    @Inject
    public CenterPanelController(Provider<RightPanel> rightPanelProvider, CenterRightPanel centerRightPanel, EventBus eventBus) {
        this.centerRightPanel = centerRightPanel;
        this.eventBus = eventBus;
        this.rightPanelProvider = rightPanelProvider;
        eventBus.register(this);
    }


    @Subscribe
    public void onChatSelectedEvent(ChatSelectedEvent event) {
        Long chatId = event.getChatId();
        centerRightPanel.setVisible(true);     // Hiển thị panel
        logger.info("centerRightPanel visible: " + centerRightPanel.isVisible());
        logger.info("centerRightPanel size: " + centerRightPanel.getSize());
        centerRightPanel.revalidate();
        centerRightPanel.repaint();
        centerRightPanel.getParent().revalidate();
        centerRightPanel.getParent().repaint();// Vẽ lại
        logger.info(">>>>>>>>Chat selected: " + chatId);
        logger.info(">>>>>>>>centerRightPanel visible: " + centerRightPanel.isVisible());
        logger.info(">>>>>>>>centerRightPanel size: " + centerRightPanel.getSize());
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
