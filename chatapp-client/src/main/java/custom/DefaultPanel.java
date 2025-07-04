package custom;

import javax.swing.*;
import java.awt.*;

public class DefaultPanel extends JPanel {
    public DefaultPanel() {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel("Chọn một cuộc trò chuyện để bắt đầu");
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        label.setForeground(Color.GRAY);

        add(label);
    }
}