package view;

import model.ChatItem;

import javax.swing.*;
import java.awt.*;

class ChatItemRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        if (value instanceof ChatItem) {
            ChatItem item = (ChatItem) value;
            JLabel avatarLabel = new JLabel("👤"); // Placeholder cho avatar
            JLabel usernameLabel = new JLabel(item.getUsername());
            JLabel lastMessageLabel = new JLabel(item.getLastMessage() != null ? item.getLastMessage() : "");
            JLabel timeLabel = new JLabel(item.getFormattedTime());

            panel.add(avatarLabel, BorderLayout.WEST);
            panel.add(usernameLabel, BorderLayout.NORTH);
            panel.add(lastMessageLabel, BorderLayout.CENTER);
            panel.add(timeLabel, BorderLayout.EAST);

            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
                usernameLabel.setForeground(list.getSelectionForeground());
                lastMessageLabel.setForeground(list.getSelectionForeground());
                timeLabel.setForeground(list.getSelectionForeground());
            } else {
                panel.setBackground(list.getBackground());
                usernameLabel.setForeground(list.getForeground());
                lastMessageLabel.setForeground(list.getForeground());
                timeLabel.setForeground(list.getForeground());
            }
        }
        return panel;
    }
}
