package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainVideoFrame extends JFrame {
    private VideoPanel videoPanel;

    public MainVideoFrame() {
        super("Video Call");

        videoPanel = new VideoPanel();
        setLayout(new BorderLayout());
        add(videoPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateFrame(BufferedImage frame) {
        videoPanel.updateImage(frame);
    }
}
