package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class VideoPanel extends JPanel {
    private final Logger logger = Logger.getLogger(VideoPanel.class.getName());
    private BufferedImage image;

    public void updateImage(BufferedImage img) {
        logger.info("Updating image");
        this.image = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null)
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}
