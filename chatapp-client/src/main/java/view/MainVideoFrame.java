package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainVideoFrame extends JFrame {
    public final VideoPanel localPanel;
    public final VideoPanel remotePanel;

    public MainVideoFrame() {
        setTitle("Video Call");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2)); // 1 dòng, 2 cột

        localPanel = new VideoPanel();
        remotePanel = new VideoPanel();

        add(localPanel);
        add(remotePanel);

        setSize(800, 600); // hoặc tùy chỉnh
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public VideoPanel getLocalPanel() {
        return localPanel;
    }
}
