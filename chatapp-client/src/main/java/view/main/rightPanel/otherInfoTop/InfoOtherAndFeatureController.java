package view.main.rightPanel.otherInfoTop;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import controllers.RenameGroupController;
import di.BaseController;
import event.FullNameUpdateEvent;
import event.GroupRenamedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.login.TokenManager;
import view.main.UserToken;
import event.ChatSelectedEvent;
import view.main.dialog.Rename.RenameGroupDialog;
import view.main.dialog.Rename.RenameGroupImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class InfoOtherAndFeatureController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(InfoOtherAndFeatureController.class);
    private final InfoOtherAndFeature infoOtherAndFeature;
    private final RenameGroupImpl renameGroupImpl;
    private final WebRTCManager webRTCManager;
    private RenameGroupDialog renameGroupDialog;
    private final RenameGroupController renameGroupController;

    private Long chatId;
    private Long otherUserId;
    private Long userId;
    private String jwtToken;
    private String Name;
    private String type;


    @Inject
    public InfoOtherAndFeatureController(InfoOtherAndFeature infoOtherAndFeature, WebRTCManager webRTCManager, EventBus eventBus, RenameGroupImpl renameGroupImpl, RenameGroupController renameGroupController) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.webRTCManager = webRTCManager;
        this.renameGroupImpl = renameGroupImpl;
        this.renameGroupController = renameGroupController;

        eventBus.register(this);
        initializeListeners();
    }


    @Subscribe
    public void onJwtToken(UserToken userToken) {
        this.jwtToken = TokenManager.getAccessToken();
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onGroupRenamed(GroupRenamedEvent event) {
            infoOtherAndFeature.setUsername(event.getNewGroupName());
            infoOtherAndFeature.revalidate();
            infoOtherAndFeature.repaint();
    }


    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        this.chatId = event.getChatId();
        this.otherUserId = event.getUserId();
        this.type = event.getType();

        boolean isGroup = "GROUP".equalsIgnoreCase(type);
        infoOtherAndFeature.getRenameButton().setVisible(isGroup);
    }
    @Subscribe
    public void onFullNameUpdate(FullNameUpdateEvent event) {
        this.Name = event.getFullName();
        logger.info("NNNNNNNNNNNName: " + event.getFullName());
        if(Name != null && !Name.isEmpty()) {
            infoOtherAndFeature.getUserOtherName().setText(event.getFullName());
            logger.info("NNNNNNNNNNNNNNNNNName: " + event.getFullName());
        }
        if(event.getImageUrl() != null) {
            setAvatarIcon(event.getImageUrl());
            logger.info("update imageeeeeeeeeeeeeeeeeee");
        }
    }

    private void setAvatarIcon(String imageUrl) {
        try {
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

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

            infoOtherAndFeature.renameGroup(e -> {
                    renameGroupDialog = new RenameGroupDialog((JFrame) SwingUtilities.getWindowAncestor(infoOtherAndFeature), Name);
                    renameGroupController.setRenameGroup(renameGroupDialog);
                    renameGroupDialog.setVisible(true);
            });
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
