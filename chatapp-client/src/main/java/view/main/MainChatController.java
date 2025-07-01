package view.main;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import utility.WebSocketClientManager;
import view.ApiResult;
import view.login.AutoRefreshScheduler;
import view.login.TokenManager;

import javax.swing.*;

public class MainChatController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainChatController.class);
    private String username;
    private Long userId;
    private String avatarUrl;
    private String jwtToken;

    private final MainChatView mainChatView;
    private final EventBus eventBus;
    private WebSocketClientManager webSocketClientManager;


    @Inject
    public MainChatController(MainChatView mainChatView,WebSocketClientManager webSocketClientManager, EventBus eventBus) {
        this.mainChatView = mainChatView;
        this.webSocketClientManager = webSocketClientManager;
        this.eventBus = eventBus;

    }

    @Override
    protected void setupDependencies() {

    }

    @Override
    public void activate(Object... params) {
        this.userId = (Long) params[0];
        this.username = (String) params[1];
        this.avatarUrl = (String) params[2];
        this.jwtToken = (String) params[3];

        if (mainChatView == null) {
            setupDependencies(); // Chỉ setup lần đầu
        }
        mainChatView.setUsername(this.username);
        mainChatView.setVisible(true);

        LOGGER.info("Avatar url: " + avatarUrl);
        eventBus.post(new UserToken(TokenManager.getAccessToken(), userId, username, avatarUrl));
        AutoRefreshScheduler.start();

        ApiResult<String> result = webSocketClientManager.setupWebSocket(TokenManager.getAccessToken(), username);

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(mainChatView, "Connected to server");
        }else {
            JOptionPane.showMessageDialog(mainChatView, result.getError().getErrors().indexOf(0));
        }
    }

    @Override
    public void deactivate() {
        mainChatView.setVisible(false);

    }
}
