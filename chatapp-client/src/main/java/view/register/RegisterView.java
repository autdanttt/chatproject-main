package view.register;


import custom.RoundedButton;
import custom.RoundedPasswordField;
import custom.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame {
    private final JTextField emailField, fullNameField, passwordField, confirmPasswordField;
    private final JButton sendButton;
    private final JButton cancelButton;

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
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Đăng Kí".toUpperCase());
        title.setFont(new Font("Montserrat", Font.BOLD, 25));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(title, gbc);

        JLabel l0 = new JLabel("Nhập email");
        l0.setFont(new Font("Montserrat", Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(l0, gbc);

        emailField = new RoundedTextField(20);
        emailField.setPreferredSize(new Dimension(200, 40));
        emailField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add(emailField, gbc);

        JLabel l1 = new JLabel("Nhập tên người dùng");
        l1.setFont(new Font("Montserrat", Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(l1, gbc);

        fullNameField = new RoundedTextField(20);
        fullNameField.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(fullNameField, gbc);

        JLabel passwordLabel = new JLabel("Nhập mật khẩu");
        passwordLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
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
        confirmPasswordLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        contentPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new RoundedPasswordField(20);
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

    public String getEmail() {
        return emailField.getText();
    }

    public String getFullName() {
        return fullNameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }


    public void addRegisterButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);

    }

    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}
