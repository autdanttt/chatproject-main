package view.main.rightPanel.otherInfoTop;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import custom.CreateButton;
import event.FullNameUpdateEvent;
import utility.WebRTCManager;
//import view.main.leftPanel.chatlist.ChatSelectedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class InfoOtherAndFeature extends JPanel {
    private final JLabel userOtherName;
    private final JButton callVideoButton;
    private final JButton callPhoneButton;
    private JLabel avatarOtherLabel;

    private final WebRTCManager webRTCManager;
    private Long chatId;
    private Long otherUserId;
    private Long userId;

    private String basePath = new File(System.getProperty("user.dir")).getParent();

    @Inject
    public InfoOtherAndFeature(WebRTCManager webRTCManager, EventBus eventBus) {
        this.webRTCManager = webRTCManager;

        setLayout(new GridLayout(1, 2));
        setPreferredSize(new Dimension(600, 70));
        setBackground(Color.WHITE);

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBackground(Color.WHITE);

        JPanel avatarOtherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 11));
        avatarOtherPanel.setBackground(Color.WHITE);
        avatarOtherLabel = new JLabel();
        avatarOtherLabel.setPreferredSize(new Dimension(48, 48));
        userOtherName = new JLabel("None");
        userOtherName.setFont(new Font("Montserrat", Font.PLAIN, 14));

        avatarOtherPanel.add(avatarOtherLabel);
        avatarOtherPanel.add(userOtherName);

        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 11));
        featurePanel.setBackground(Color.WHITE);

        callVideoButton = new CreateButton(basePath + "/images/CALL_VIDEO.png");

        callPhoneButton = new CreateButton(basePath + "/images/CALL_PHONE.png");

        featurePanel.add(callPhoneButton);
        featurePanel.add(callVideoButton);

        add(avatarOtherPanel);
        add(featurePanel);

        eventBus.register(this);

//        initializeListeners();
    }

//    private void initializeListeners() {
//        callVideoButton.addActionListener(e -> startVideoCall());
//    }

//    private void initializeListeners() {
//        callVideoButton.addActionListener(e -> startVideoCall());
//    }
//
//    private void startVideoCall() {
//        SwingUtilities.invokeLater(() -> {
//            if (chatId != null && userId != null) {
//                MainVideoFrame videoFrame = new MainVideoFrame();
//                videoFrame.setVisible(true);
//
//                webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);
//                webRTCManager.initialize(otherUserId);
//                webRTCManager.addMediaStream(1);
//                webRTCManager.createOffer(otherUserId);
//            } else {
//                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi.");
//            }
//        });
//    }

    public JLabel getUserOtherName() {
        return userOtherName;
    }

    public JLabel getAvatarOtherLabel() {
        return avatarOtherLabel;
    }

//
//    @Subscribe
//    public void onChatSelected(ChatSelectedEvent event) {
//        this.chatId = event.getChatId();
//        this.otherUserId = event.getUserId();
//    }
//

//    @Subscribe
//    public void onUsernameUpdate(FullNameUpdateEvent event) {
//        userOtherName.setText(event.getFullName());
//    }

    public void setUsername(String username) {
        userOtherName.setText(username);
    }

    public void addVideoButtonListener(ActionListener listener) {
        callVideoButton.addActionListener(listener);
    }

}
