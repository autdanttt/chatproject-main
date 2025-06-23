package view.main.rightPanel.otherInfoTop;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.UsernameUpdateEvent;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.main.UserToken;
import view.main.leftPanel.chatlist.ChatSelectedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InfoOtherAndFeature extends JPanel {
    private final JLabel userOtherName;
    private final JButton callVideoButton;
    private final JButton callPhoneButton;

    private final WebRTCManager webRTCManager;
    private Long chatId;
    private Long otherUserId;
    private Long userId;

    @Inject
    public InfoOtherAndFeature(WebRTCManager webRTCManager, EventBus eventBus) {
        this.webRTCManager = webRTCManager;

        setLayout(new GridLayout(1, 2));
        setPreferredSize(new Dimension(500, 70));
        setBackground(Color.WHITE);

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBackground(Color.WHITE);

        JPanel avatarOtherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 11));
        avatarOtherPanel.setBackground(Color.WHITE);
        JLabel avatarOtherLabel = new JLabel();
        avatarOtherLabel.setPreferredSize(new Dimension(48, 48));
        avatarOtherLabel.setIcon(new ImageIcon("D:/chat_ui/images/Group 14.png"));
        userOtherName = new JLabel("None");
        userOtherName.setFont(new Font("Montserrat", Font.PLAIN, 14));

        avatarOtherPanel.add(avatarOtherLabel);
        avatarOtherPanel.add(userOtherName);

        JPanel callPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        callPanel.setBackground(Color.WHITE);

        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 11));
        featurePanel.setBackground(Color.WHITE);

        callVideoButton = new JButton();
        callVideoButton.setIcon(new ImageIcon("D:/chat_ui/images/CALL_VIDEO.png"));
        callVideoButton.setPreferredSize(new Dimension(48, 48));
        callVideoButton.setBorder(BorderFactory.createEmptyBorder());
        callVideoButton.setContentAreaFilled(false);
        callVideoButton.setFocusPainted(false);
        callVideoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        callPhoneButton = new JButton();
        callPhoneButton.setIcon(new ImageIcon("D:/chat_ui/images/CALL_PHONE.png"));
        callPhoneButton.setPreferredSize(new Dimension(48, 48));
        callPhoneButton.setBorder(BorderFactory.createEmptyBorder());
        callPhoneButton.setContentAreaFilled(false);
        callPhoneButton.setFocusPainted(false);
        callPhoneButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        featurePanel.add(callPhoneButton);
        featurePanel.add(callVideoButton);

        add(avatarOtherPanel);
        add(featurePanel);

        eventBus.register(this);

        initializeListeners();
    }

    private void initializeListeners() {
        callVideoButton.addActionListener(e -> startVideoCall());
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
                JOptionPane.showMessageDialog(null, "Hãy chọn một người để gọi.");
            }
        });
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
    public void onUsernameUpdate(UsernameUpdateEvent event) {
        userOtherName.setText(event.getUsername());
    }

    public void setUsername(String username) {
        userOtherName.setText(username);
    }

    public void addVideoButtonListener(ActionListener listener) {
        callVideoButton.addActionListener(listener);
    }

}
