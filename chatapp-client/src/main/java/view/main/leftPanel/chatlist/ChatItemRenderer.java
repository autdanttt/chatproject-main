package view.main.leftPanel.chatlist;

import model.ChatItem;

import javax.swing.*;
import java.awt.*;

public class ChatItemRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof ChatItem item)) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(true);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon avatarIcon = new ImageIcon("D:/chatproject-main/images/Group 16.png");
        if (item.getAvatar() != null && !item.getAvatar().isEmpty()) {
            avatarIcon = new ImageIcon(item.getAvatar());
        }

        if (avatarIcon != null) {
            Image scaledImage = avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            avatarLabel.setText(" ");
            avatarLabel.setFont(new Font("Montserrat", Font.PLAIN, 24));
        }

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);
        JLabel usernameLabel = new JLabel(item.getUsername());
        usernameLabel.setFont(new Font("Montserrat", Font.BOLD, 14));

        String lastMessage = item.getLastMessage() != null ? item.getLastMessage() : "";
        lastMessage = shortenText(lastMessage, 40);

        JLabel lastMessageLabel = new JLabel(lastMessage);
        lastMessageLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        lastMessageLabel.setForeground(Color.decode("#E9E9E9"));

        centerPanel.add(usernameLabel);
        centerPanel.add(lastMessageLabel);

        JLabel timeLabel = new JLabel(item.getFormattedTime());
        timeLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        timeLabel.setForeground(Color.decode("#E9E9E9"));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(avatarLabel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.EAST);

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            usernameLabel.setForeground(list.getSelectionForeground());
            lastMessageLabel.setForeground(list.getSelectionForeground());
            timeLabel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            usernameLabel.setForeground(list.getForeground());
            lastMessageLabel.setForeground(Color.GRAY);
            timeLabel.setForeground(Color.GRAY);
        }
        return panel;
    }

    private String shortenText(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

}
