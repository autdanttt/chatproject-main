package view;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVideoFrameController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MainVideoFrameController.class);
    private MainVideoFrame mainVideoFrame;
    private EventBus eventBus;
    @Inject
    public MainVideoFrameController(MainVideoFrame mainVideoFrame, EventBus eventBus) {
        this.mainVideoFrame = mainVideoFrame;
        this.eventBus = eventBus;
        initializeListeners();
    }

    private void initializeListeners() {
        mainVideoFrame.addHangupButtonListener(e -> {
            logger.info("Hangup button pressed");
            callEnded();
        });
    }

    private void callEnded() {
        logger.info("Click ended call");
        eventBus.post(new CallEndedEvent());
        mainVideoFrame.closeFrame();
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

    public MainVideoFrame getMainVideoFrame() {
        return mainVideoFrame;
    }
}
