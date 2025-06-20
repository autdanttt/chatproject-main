package view.main.rightpanel.usernameinfo.usernamestatus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.UsernameUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;

public class UsernameStatusController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsernameStatusController.class);
    private final UsernameStatusPanel usernameStatusPanel;

    @Inject
    public UsernameStatusController(UsernameStatusPanel usernameStatusPanel, EventBus eventBus) {
        this.usernameStatusPanel = usernameStatusPanel;
        eventBus.register(this);
    }

    @Subscribe
    public void onUsernameUpdate(UsernameUpdateEvent event) {
        LOGGER.info("Received username " + event.getUsername());
        usernameStatusPanel.setUsername(event.getUsername());
    }

    @Override
    protected void setupDependencies() {
//        usernameStatusPanel.setAvatarPath("d7d2b8d1-f350-49aa-8516-105ed6b54f69.jpg");
    }

    @Override
    public void activate(Object... params) {
//        String username = (String) params[0];
////        String status = (String) params[1];
////        usernameStatusPanel.getStatusLabel().setText(status);
//        usernameStatusPanel.setUsername(username);
    }

    @Override
    public void deactivate() {

    }
}
