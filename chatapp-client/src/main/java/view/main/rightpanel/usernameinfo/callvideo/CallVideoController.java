package view.main.rightpanel.usernameinfo.callvideo;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import di.BaseController;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.main.UserToken;
import view.main.chatlist.chatlist.ChatSelectedEvent;

import javax.swing.*;
import java.util.logging.Logger;

public class CallVideoController extends BaseController {
    private final Logger LOGGER = Logger.getLogger(CallVideoController.class.getName());
    private final CallVideoPanel callVideoPanel;
    private final WebRTCManager webRTCManager;
    private Long chatId;
    private Long otherUserId;
    private Long userId;

    @Inject
    public CallVideoController(CallVideoPanel callVideoPanel, WebRTCManager webRTCManager, EventBus eventBus) {
        this.callVideoPanel = callVideoPanel;
        this.webRTCManager = webRTCManager;
        eventBus.register(this);
        initializeListeners();
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        LOGGER.info("Received JWT token: " + userToken.getJwtToken());
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        LOGGER.info("Received chat selected: " + event.getChatId());
        this.chatId = event.getChatId();
        this.otherUserId = event.getUserId();
    }


    @Override
    protected void setupDependencies() {


    }

    private void initializeListeners() {
        callVideoPanel.addVideoButtonListener(e->startVideoCall());
    }

    private void startVideoCall() {
        SwingUtilities.invokeLater(()->{

            MainVideoFrame videoFrame = new MainVideoFrame();
            videoFrame.setVisible(true);

            if(chatId != null &&  userId != null) {
                // 1. Set panel
                webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);

                // 2. Khởi tạo WebRTC PeerConnection + Factory
                webRTCManager.initialize(otherUserId);

                // 3. Thêm media track (audio/video)
                webRTCManager.addMediaStream(1);

                // 4. Gửi SDP offer
                webRTCManager.createOffer(otherUserId);

            }else {
                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi");

            }
        });
    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
