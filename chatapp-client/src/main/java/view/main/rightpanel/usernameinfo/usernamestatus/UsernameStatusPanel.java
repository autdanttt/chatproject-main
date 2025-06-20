package view.main.rightpanel.usernameinfo.usernamestatus;

import javax.swing.*;
import java.awt.*;


public class UsernameStatusPanel extends JPanel {
    private final JLabel usernameLabel;

    public UsernameStatusPanel() {
        setLayout(new BorderLayout(5, 5));
        usernameLabel = new JLabel("No username");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel, BorderLayout.CENTER);
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }
}

//public class UsernameStatusPanel extends JPanel {
//    private JLabel avatarLabel;
//    private JLabel nameLabel;
//    private JLabel statusLabel;
//    private String username;
////    private String avatarPath;
//    private String status;
//    private ImageIcon statusIcon;
//
//
//    public UsernameStatusPanel() {
//        setLayout(new BorderLayout(10, 10)); // Sử dụng BorderLayout với khoảng cách
//        setBackground(new Color(240, 248, 255)); // Màu nền nhạt (light blue)
//        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding cho panel
//        setPreferredSize(new Dimension(250, 80));
//
//        avatarLabel = new JLabel();
////        setAvatarImage(avatarPath);
//        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Padding bên phải
//        add(avatarLabel, BorderLayout.WEST);
//
//        // Khởi tạo panel cho tên và trạng thái
//        JPanel textPanel = new JPanel(new GridBagLayout());
//        textPanel.setOpaque(false); // Làm trong suốt để thấy nền panel chính
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Nhãn tên người dùng
//        nameLabel = new JLabel(username);
//        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
//        textPanel.add(nameLabel, gbc);
//
//
//        // Nhãn trạng thái với biểu tượng
//        statusIcon = createStatusIcon(status);
//        statusLabel = new JLabel(status + " ", statusIcon, JLabel.LEFT);
//        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//        statusLabel.setForeground(Color.GRAY);
//        gbc.gridy = 1;
//        textPanel.add(statusLabel, gbc);
//
//        add(textPanel, BorderLayout.CENTER);
//    }
////    private void setAvatarImage(String avatarPath) {
////        URL imageUrl = getClass().getResource(avatarPath);
////        if (imageUrl != null) {
////            ImageIcon icon = new ImageIcon(imageUrl);
////            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Điều chỉnh kích thước
////            avatarLabel.setIcon(new ImageIcon(img));
////        } else {
////            avatarLabel.setText("No Image"); // Hiển thị nếu không tải được ảnh
////        }
////    }
//    private ImageIcon createStatusIcon(String status) {
//        if ("Đang hoạt động".equals(status)) {
//            return new ImageIcon(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB) {
//                {
//                    Graphics2D g2d = (Graphics2D) getGraphics();
//                    g2d.setColor(Color.GREEN); // Màu xanh lá
//                    g2d.fillOval(0, 0, 10, 10); // Vẽ vòng tròn
//                    g2d.dispose();
//                }
//            });
//        }
//        return null; // Trạng thái khác có thể thêm biểu tượng khác
//    }
//
//    // Phương thức để cập nhật trạng thái
//    public void updateStatus(String status) {
//        statusLabel.setText(status + " ");
//        statusLabel.setIcon(createStatusIcon(status));
//        revalidate();
//        repaint();
//    }
//
//    public JLabel getAvatarLabel() {
//        return avatarLabel;
//    }
//
//    public JLabel getNameLabel() {
//        return nameLabel;
//    }
//
//    public JLabel getStatusLabel() {
//        return statusLabel;
//    }
////
////    public void setAvatarPath(String avatarPath) {
////        this.avatarPath = avatarPath;
////    }
//}
