package view.main;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import di.BaseController;
import utility.WebSocketClientManager;
import view.ApiResult;

import javax.swing.*;

public class MainChatController extends BaseController {
    private String username;
    private Long userId;
    private String jwtToken;

    private final MainChatView mainChatView;
    private final EventBus eventBus;
    private WebSocketClientManager webSocketClientManager;


    @Inject
    public MainChatController(MainChatView mainChatView, WebSocketClientManager webSocketClientManager, EventBus eventBus) {
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
        this.jwtToken = (String) params[2];

        if (mainChatView == null) {
            setupDependencies();
        }
        mainChatView.setUsername(this.username);
        eventBus.post(new UserToken(jwtToken, userId, username));
        ApiResult<String> result = webSocketClientManager.setupWebSocket(jwtToken, username);
    }

    @Override
    public void deactivate() {
        mainChatView.setVisible(false);

    }
}
