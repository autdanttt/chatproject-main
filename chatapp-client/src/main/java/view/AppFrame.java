package view;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    private JPanel contentPanel;

    public AppFrame() {
        setTitle("Ứng dụng Chat");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel);
    }

    public void setContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

}
