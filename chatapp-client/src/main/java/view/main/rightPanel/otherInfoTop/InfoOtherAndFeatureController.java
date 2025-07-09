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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import utility.OtherUserStatusEvent;
import utility.StatusNotification;
import utility.WebRTCManager;
import view.ErrorDTO;
import view.login.TokenManager;
import view.main.UserToken;
import event.ChatSelectedEvent;
import view.main.dialog.Rename.RenameGroupDialog;
import view.main.dialog.Rename.RenameGroupService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;

public class InfoOtherAndFeatureController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(InfoOtherAndFeatureController.class);
    private final InfoOtherAndFeature infoOtherAndFeature;
    private final CallVideoService callVideoService;
    private final StatusUserService statusUserService;
    private RenameGroupDialog renameGroupDialog;
    private final RenameGroupController renameGroupController;

    private Long chatId;
    private Long otherUserId;
    private Long userId;
    private String fullName;
    private String type;
    private Timer offlineStatusTimer;
    @Inject
    public InfoOtherAndFeatureController(InfoOtherAndFeature infoOtherAndFeature, StatusUserService statusUserService, CallVideoService callVideoService, RenameGroupService renameGroupService,RenameGroupController renameGroupController, EventBus eventBus) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.callVideoService = callVideoService;
        this.statusUserService = statusUserService;
        this.renameGroupController = renameGroupController;

        eventBus.register(this);
        initializeListeners();
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
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
        logger.info("Received image " + event.getImageUrl());
        infoOtherAndFeature.getUserOtherName().setText(event.getFullName());
        setAvatarIcon(event.getImageUrl());
        fullName = event.getFullName();
        if(type.equals("CHAT")) {
            logger.info("Received type " + type);
            fetchStatus(otherUserId);
        }else {
            logger.info("Received type " + type);
            infoOtherAndFeature.getStatusOther().setText(" ");
        }

        if(fullName != null && !fullName.isEmpty()) {
            infoOtherAndFeature.getUserOtherName().setText(event.getFullName());
            logger.info("NNNNNNNNNNNNNNNNNName: " + event.getFullName());
        }
        if(event.getImageUrl() != null) {
            setAvatarIcon(event.getImageUrl());
            logger.info("update imageeeeeeeeeeeeeeeeeee");
        }
    }

//    @Subscribe
//    public void onOtherUserStatusEvent(StatusNotification event) {
//        String status = event.getStatus();
//        type = "CHAT";
//
//        logger.info("Received user status: type=" + type + ", status=" + status);
//
//        if (!"CHAT".equals(type)) {
//            infoOtherAndFeature.getStatusOther().setText(" ");
//            return;
//        }
//
//        if ("online".equals(status)) {
//            infoOtherAndFeature.getStatusOther().setText("🟢 " + status);
//        } else {
//            infoOtherAndFeature.getStatusOther().setText("⚫ " + status);
//        }
//    }
//

    @Subscribe
    public void onOtherUserStatusEvent(StatusNotification event) {
        String status = event.getStatus();
        String email = event.getEmail();
        String lastSeen = event.getLast();

        if (!"CHAT".equals(type)) {
            infoOtherAndFeature.getStatusOther().setText(" ");
            return;
        }
        updateStatusDisplay(status, lastSeen);

    }



    private void fetchStatus(Long otherUserId) {
        try {
            StatusNotification userStatus = statusUserService.fetchStatus(otherUserId, TokenManager.getAccessToken());
            updateStatusDisplay(userStatus.getStatus(), userStatus.getLast());
        } catch (Exception e) {
            logger.warn("❌ Không thể lấy trạng thái userId=" + otherUserId, e);
            infoOtherAndFeature.getStatusOther().setText("Không xác định");
        }
    }
    private void updateStatusDisplay(String status, String lastSeenStr) {
        // Hủy timer cũ nếu có
        if (offlineStatusTimer != null) {
            offlineStatusTimer.cancel();
            offlineStatusTimer = null;
        }

        if ("online".equalsIgnoreCase(status)) {
            infoOtherAndFeature.getStatusOther().setText("🟢 Đang hoạt động");
            return;
        }

        long parsedLastSeen;
        try {
            parsedLastSeen = Long.parseLong(lastSeenStr);
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Không parse được lastSeen: {}", lastSeenStr, e);
            parsedLastSeen = System.currentTimeMillis();
        }

        final long lastSeenMillis = parsedLastSeen;
       // Nếu offline → khởi tạo timer cập nhật thời gian
        offlineStatusTimer = new Timer();
        offlineStatusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long minutesAgo = (System.currentTimeMillis() - lastSeenMillis) / 60000;

                String text;
                if (minutesAgo < 1) {
                    text = "⚫ Vừa xong";
                } else if (minutesAgo < 60) {
                    text = "⚫ " + minutesAgo + " phút trước";
                } else {
                    long hoursAgo = minutesAgo / 60;
                    text = "⚫ " + hoursAgo + " giờ trước";
                }

                SwingUtilities.invokeLater(() ->
                        infoOtherAndFeature.getStatusOther().setText(text)
                );
            }
        }, 0, 60 * 1000);
    }


    private void setAvatarIcon(String imageUrl) {
        logger.info("Setting avatar icon to " + imageUrl);
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
                    renameGroupDialog = new RenameGroupDialog((JFrame) SwingUtilities.getWindowAncestor(infoOtherAndFeature), fullName);
                    renameGroupController.setRenameGroup(renameGroupDialog);
                    renameGroupDialog.setVisible(true);
            });
        }




    private void startVideoCall() {
        SwingUtilities.invokeLater(() -> {
            if (chatId != null && userId != null && otherUserId != null) {
                ResponseEntity<?> response = callVideoService.sendCallRequest(userId, otherUserId,fullName,TokenManager.getAccessToken());
                if (response.getStatusCode() == HttpStatus.OK) {
                    Map<String, String> body = (Map<String, String>) response.getBody();
                    logger.info("Received video call from user id: ");
                    JOptionPane.showMessageDialog(null, body.get("message"), "Info", JOptionPane.INFORMATION_MESSAGE);
                    // Hiển thị MainVideoFrame nhưng chưa khởi tạo WebRTC
//                    MainVideoFrame videoFrame = new MainVideoFrame();
//                    videoFrame.setVisible(true);
//                    videoFrame.showWaitingState(); // Giả định MainVideoFrame có phương thức hiển thị trạng thái chờ
                } else {
                    ErrorDTO error = (ErrorDTO) response.getBody();
                    String errorMessage = error.getErrors().isEmpty() ? "Error sending call request" : String.join("; ", error.getErrors());
                    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi");
            }
        });
    }

//    private void startVideoCall() {
//        SwingUtilities.invokeLater(() -> {
//            if (chatId != null && userId != null) {
//                MainVideoFrame videoFrame = new MainVideoFrame();
//                videoFrame.setVisible(true);
//                webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);
//                webRTCManager.initialize(otherUserId);
//                webRTCManager.addMediaStream(1);
//                webRTCManager.createOffer(otherUserId);
//            } else {
//                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi");
//            }
//        });
//
//    }


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
