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
        setLayout(new GridLayout(1, 2));
        setLocationRelativeTo(null);

        localPanel = new VideoPanel();
        remotePanel = new VideoPanel();

        add(localPanel);
        add(remotePanel);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public VideoPanel getLocalPanel() {
        return localPanel;
    }
}
