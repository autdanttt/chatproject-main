package view.main.rightPanel.otherInfoTop;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import di.BaseController;
import event.UsernameUpdateEvent;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.login.TokenManager;
import view.main.UserToken;
import view.main.leftPanel.chatlist.ChatSelectedEvent;

import javax.swing.*;
import java.util.logging.Logger;

public class InfoOtherAndFeatureController extends BaseController {
    private final InfoOtherAndFeature infoOtherAndFeature;
    private final WebRTCManager webRTCManager;

    private Long chatId;
    private Long otherUserId;
    private Long userId;

    @Inject
    public InfoOtherAndFeatureController(InfoOtherAndFeature infoOtherAndFeature, WebRTCManager webRTCManager, EventBus eventBus) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.webRTCManager = webRTCManager;

        eventBus.register(this);
        initializeListeners();
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        this.chatId = event.getChatId();
        this.otherUserId = event.getUserId();
    }


    @Override
    protected void setupDependencies() {


    }

    private void initializeListeners() {
        infoOtherAndFeature.addVideoButtonListener(e -> startVideoCall());
    }

    private void startVideoCall() {
        SwingUtilities.invokeLater(() -> {
            if (chatId != null && userId != null) {
                MainVideoFrame videoFrame = new MainVideoFrame();
                videoFrame.setVisible(true);

                webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);
                webRTCManager.initialize(otherUserId);
                webRTCManager.addMediaStream(1);
                webRTCManager.createOffer(otherUserId);
            } else {
                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi");
            }
        });
    }


    @Subscribe
    public void onUsernameUpdate(UsernameUpdateEvent event) {
        infoOtherAndFeature.setUsername(event.getUsername());
    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
