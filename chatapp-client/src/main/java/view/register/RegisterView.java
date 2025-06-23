package view.register;


import custom.RoundedButton;
import custom.RoundedPasswordField;
import custom.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame {
    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JButton sendButton;
    private final JButton cancelButton;
    private String username;

    public RegisterView() {
        setTitle("Đăng ký");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Tên người dùng");
        usernameLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(usernameLabel, gbc);

        usernameField = new RoundedTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 40));
        usernameField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add(usernameField, gbc);

        JLabel phoneLabel = new JLabel("Số điện thoại");
        phoneLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(phoneLabel, gbc);

        JTextField phoneField = new RoundedTextField(20);
        phoneField.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(phoneField, gbc);

        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        contentPanel.add(passwordLabel, gbc);

        passwordField = new RoundedPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 40));
        passwordField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        contentPanel.add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Nhập lại mật khẩu");
        confirmPasswordLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        contentPanel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new RoundedPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(200, 40));
        confirmPasswordField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        contentPanel.add(confirmPasswordField, gbc);

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setBackground(Color.WHITE);

        cancelButton = new RoundedButton("Hủy");
        sendButton = new RoundedButton("Đăng ký");

        buttonsPanel.add(cancelButton, BorderLayout.WEST);
        buttonsPanel.add(sendButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);
        contentPanel.add(buttonsPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        setResizable(false);
        setVisible(true);
    }

    public void setUsername(String username) {
        this.username = username;
        if (usernameField != null) {
            usernameField.setText(username);
        }
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);

    }

    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
