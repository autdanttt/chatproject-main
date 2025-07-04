package view.main.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AvatarPopupMenu extends JPopupMenu {
    private JMenuItem editProfileItem;
    private JMenuItem logoutItem;

    public AvatarPopupMenu(ActionListener editProfileAction, ActionListener logoutAction) {
        editProfileItem = new JMenuItem("Sửa thông tin người dùng");
        editProfileItem.setFont(new Font("Montserrat", Font.BOLD, 12));
        editProfileItem.setPreferredSize(new Dimension(180, 30));

        logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.setFont(new Font("Montserrat", Font.BOLD, 12));
        logoutItem.setPreferredSize(new Dimension(180, 30));

        add(editProfileItem);
        add(logoutItem);

        editProfileItem.addActionListener(editProfileAction);
        logoutItem.addActionListener(logoutAction);
    }

    public void addEditProfileActionListener(ActionListener e) {
        editProfileItem.addActionListener(e);
    }

    public void addLogoutActionListener(ActionListener e) {
        logoutItem.addActionListener(e);
    }
}
