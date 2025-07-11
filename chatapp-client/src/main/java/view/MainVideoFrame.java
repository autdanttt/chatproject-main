package view;

import custom.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MainVideoFrame extends JFrame {
    public final VideoPanel localPanel;
    public final VideoPanel remotePanel;
    private final JLabel statusLabel;
    private final JButton hangupButton;

    public MainVideoFrame() {
        setTitle("Video Call");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        localPanel = new VideoPanel();
        remotePanel = new VideoPanel();
        statusLabel = new JLabel("Waiting for response...", SwingConstants.CENTER);

        // Panel chứa hai VideoPanel chia đôi đều
        JPanel videoPanelContainer = new JPanel(new GridLayout(1, 2));
        videoPanelContainer.add(localPanel);
        videoPanelContainer.add(remotePanel);

        // Thêm nút hangup
        hangupButton = new RoundedButton("Kết thúc cuộc gọi");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(hangupButton);

        add(statusLabel, BorderLayout.NORTH);
        add(videoPanelContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addHangupButtonListener(ActionListener listener) {
        hangupButton.addActionListener(listener);
    }

    public void showWaitingState() {
        statusLabel.setText("Waiting for response...");
    }

    public void showActiveState() {
        statusLabel.setText("Call connected");
    }

    public VideoPanel getLocalPanel() {
        return localPanel;
    }

    public void closeFrame() {
        localPanel.clear();
        remotePanel.clear();
//        this.dispose();
        this.setVisible(false); // Không dispose
    }
}

