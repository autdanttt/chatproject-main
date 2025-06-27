package view.main.rightPanel.components;

import view.main.dialog.UserMenu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HeaderPanel extends JPanel {
    private JLabel userLabel;
    private final UserMenu userMenu = new UserMenu();

    public HeaderPanel() {
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

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setIcon(new ImageIcon("D:/chat_ui/images/Group 17.png"));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!userMenu.isVisible()) {
                    userMenu.showMenu(avatarLabel, -100, avatarLabel.getHeight());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!userMenu.isVisible()) {
                    userMenu.showMenu(avatarLabel, -100, avatarLabel.getHeight());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (userMenu.isVisible()) {
                    userMenu.setVisible(false);
                }
            }
        });

        userMenu.addChangePasswordListener(evt -> {
            JOptionPane.showMessageDialog(HeaderPanel.this, "Chức năng đổi mật khẩu sẽ mở ở đây.");
        });

        userMenu.addLogoutListener(evt -> {
            JOptionPane.showMessageDialog(HeaderPanel.this, "Bạn đã đăng xuất.");
        });

        pn2.add(userLabel, BorderLayout.CENTER);
        pn2.add(avatarLabel, BorderLayout.EAST);

        add(pn2, BorderLayout.EAST);
    }

    public void setUserName(String userName) {
        userLabel.setText(userName);
    }
}
