package view.main.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserMenu extends JPopupMenu {
    private JMenuItem changePasswordItem;
    private JMenuItem logoutItem;

    public UserMenu() {
        changePasswordItem = new JMenuItem("Đổi mật khẩu");
        logoutItem = new JMenuItem("Đăng xuất");

        add(changePasswordItem);
        add(logoutItem);
    }

    public void addChangePasswordListener(ActionListener listener) {
        changePasswordItem.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutItem.addActionListener(listener);
    }

    // Gọi phương thức này để hiển thị menu tại một component
    public void showMenu(Component invoker, int x, int y) {
        this.show(invoker, x, y);
    }
}
