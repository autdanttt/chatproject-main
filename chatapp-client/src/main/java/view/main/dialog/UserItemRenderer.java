package view.main.dialog;

import model.User;

import javax.swing.*;
import java.awt.*;


public class UserItemRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof User user)) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(true);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon avatarIcon = new ImageIcon("D:/chatproject-main/images/default_avatar.png");
//        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
//            avatarIcon = new ImageIcon(user.getAvatarPath());
//        }
//
//        if (avatarIcon != null) {
//            Image scaledImage = avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
//            avatarLabel.setIcon(new ImageIcon(scaledImage));
//        }
        Image scaledImage = avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        avatarLabel.setIcon(new ImageIcon(scaledImage));
        JLabel usernameLabel = new JLabel(user.getEmail());
        usernameLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));

        panel.add(avatarLabel, BorderLayout.WEST);
        panel.add(usernameLabel, BorderLayout.CENTER);

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            usernameLabel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            usernameLabel.setForeground(list.getForeground());
        }

        return panel;
    }
}
