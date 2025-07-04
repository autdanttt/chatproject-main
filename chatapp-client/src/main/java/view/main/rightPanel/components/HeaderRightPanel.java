package view.main.rightPanel.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderRightPanel extends JPanel {
    private JLabel userLabel;
    private JLabel avatarLabel;

    public HeaderRightPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 100));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pn2 = new JPanel(new BorderLayout());
        pn2.setBackground(Color.WHITE);

        userLabel = new JLabel();
        userLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        userLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pn2.add(userLabel, BorderLayout.CENTER);
        pn2.add(avatarLabel, BorderLayout.EAST);

        add(pn2, BorderLayout.EAST);
    }

    public JLabel getAvatarIcon() {
        return avatarLabel;
    }

    public JLabel getUserLabel() {
        return userLabel;
    }
}
