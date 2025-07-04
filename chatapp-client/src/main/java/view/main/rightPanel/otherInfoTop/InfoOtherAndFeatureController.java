package view.main.rightPanel.otherInfoTop;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import di.BaseController;
import event.FullNameUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.main.UserToken;
import event.ChatSelectedEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class InfoOtherAndFeatureController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(InfoOtherAndFeatureController.class);
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
    @Subscribe
    public void onFullNameUpdate(FullNameUpdateEvent event) {
        logger.info("Received image " + event.getImageUrl());
        infoOtherAndFeature.getUserOtherName().setText(event.getFullName());
        setAvatarIcon(event.getImageUrl());
    }

    private void setAvatarIcon(String imageUrl) {
        logger.info("Setting avatar icon to " + imageUrl);
        try {
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
            // Resize ảnh về đúng kích thước label (40x40)
            Image scaledImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

            ImageIcon icon = new ImageIcon(scaledImage);
            infoOtherAndFeature.getAvatarOtherLabel().setIcon(icon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void loadInfo(Long chatId){

    }

    @Subscribe
    public void onUsernameUpdate(FullNameUpdateEvent event) {
        infoOtherAndFeature.setUsername(event.getFullName());
    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
