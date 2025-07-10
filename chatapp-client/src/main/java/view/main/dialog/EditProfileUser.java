package view.main.dialog;

import custom.RoundedButton;
import custom.RoundedPasswordField;
import custom.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class EditProfileUser extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel avatarLabel;
    private JButton uploadButton;
    private JButton submitButton, cancelButton;

    public EditProfileUser(JFrame parent) {
        super(parent, "Chỉnh sửa thông tin người dùng", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
//        setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setPreferredSize(new Dimension(600, 100));
        JLabel title = new JLabel("Chỉnh sử thông tin người dùng".toUpperCase());
        title.setFont(new Font("Montserrat", Font.BOLD, 25));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        p1.add(title, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        JPanel fullnamePanel = new JPanel(new BorderLayout(0, 5));
        fullnamePanel.setBackground(Color.WHITE);

        JLabel fullnameLabel = new JLabel("Sửa tên người dùng");
        fullnameLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
        usernameField = new RoundedTextField(15);
        usernameField.setPreferredSize(new Dimension(220, 36));
        usernameField.setFont(new Font("Montserrat", Font.PLAIN, 14));

        fullnamePanel.add(fullnameLabel, BorderLayout.NORTH);
        fullnamePanel.add(usernameField, BorderLayout.CENTER);

        contentPanel.add(fullnamePanel, gbc);

        gbc.gridy++;
        JPanel passwordPanel = new JPanel(new BorderLayout(0, 5));
        passwordPanel.setBackground(Color.WHITE);

        JLabel passwordLabel = new JLabel("Sửa mật khẩu");
        passwordLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
        passwordField = new RoundedPasswordField(15);
        passwordField.setPreferredSize(new Dimension(220, 36));
        passwordField.setFont(new Font("Montserrat", Font.PLAIN, 14));

        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        contentPanel.add(passwordPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JPanel avatarPanel = new JPanel(new BorderLayout(5, 5));
        avatarPanel.setBackground(Color.WHITE);

        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(150, 150));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(Color.LIGHT_GRAY);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        uploadButton = new RoundedButton("Upload");

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        avatarPanel.add(uploadButton, BorderLayout.SOUTH);

        contentPanel.add(avatarPanel, gbc);


        JPanel p3 = new JPanel(new BorderLayout());
        p3.setBackground(Color.WHITE);
        p3.setPreferredSize(new Dimension(600, 100));

        cancelButton = new RoundedButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(200, 44));

        submitButton = new RoundedButton("Thực hiện yêu cầu");
        submitButton.setPreferredSize(new Dimension(200, 44));

        int panelHeight = p3.getPreferredSize().height;
        int buttonHeight = submitButton.getPreferredSize().height;
        int centerH = (panelHeight - buttonHeight) / 2 - 5;

        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, centerH));
        p4.setBackground(Color.WHITE);

        p4.add(cancelButton);
        p4.add(submitButton);

        p3.add(p4, BorderLayout.CENTER);

        mainPanel.add(p1, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(p3, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void addCacelActionListener(ActionListener e) {
        cancelButton.addActionListener(e);
    }
}
