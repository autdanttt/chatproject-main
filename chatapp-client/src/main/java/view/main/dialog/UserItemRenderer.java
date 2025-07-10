package view.main.dialog;

import custom.RoundedImageUtil;
import model.User;
import model.UserOther;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;


public class UserItemRenderer extends DefaultListCellRenderer {
    private String basePath = new File(System.getProperty("user.dir")).getParent();

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof UserOther user))
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setOpaque(true);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon avatarUser = RoundedImageUtil.loadRoundedAvatarFromURL(user.getAvatarUrl(), 40);
//        if (avatarUser != null) {
//            avatarLabel.setIcon(avatarUser);
//        } else {
//            avatarLabel.setIcon(RoundedImageUtil.loadRoundedAvatarFromFile(basePath + "/images/DEFAULT_AVATAR.png", 40));
//        }

        avatarLabel.setIcon(avatarUser);


        JLabel usernameLabel = new JLabel(user.getFullName());
        usernameLabel.setFont(new Font("Montserrat", Font.BOLD, 14));

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
