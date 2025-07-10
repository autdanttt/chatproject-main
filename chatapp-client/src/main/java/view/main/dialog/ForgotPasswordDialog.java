package view.main.dialog;

import custom.RoundedButton;
import custom.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ForgotPasswordDialog extends JDialog {
    private RoundedTextField emailField;
    private RoundedTextField otpField;
    private RoundedTextField newPasswordField;
    private RoundedTextField confirmPasswordField;
    private RoundedButton sendOtpButton;
    private RoundedButton submitButton;

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Quên mật khẩu", true);
        initUI();
    }

    private void initUI() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);

        JLabel titleLabel = new JLabel("QUÊN MẬT KHẨU", SwingConstants.CENTER);
        titleLabel.setForeground(Color.decode("#090040"));
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 25));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        contentPanel.add(titleLabel, gbc);

        // Nhập email
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Nhập email:");
        emailLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        emailLabel.setForeground(Color.decode("#090040"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(emailLabel, gbc);

        emailField = new RoundedTextField(20);
        emailField.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(emailField, gbc);

        sendOtpButton = new RoundedButton("Send");
        sendOtpButton.setFont(new Font("Montserrat", Font.BOLD, 13));
        sendOtpButton.setPreferredSize(new Dimension(80, 35));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 10, 4, 0);
        contentPanel.add(sendOtpButton, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = 2;

        // Nhập OTP
        JLabel otpLabel = new JLabel("Nhập OTP:");
        otpLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        otpLabel.setForeground(Color.decode("#090040"));
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(otpLabel, gbc);

        otpField = new RoundedTextField(20);
        otpField.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(otpField, gbc);

        // Nhập mật khẩu mới
        JLabel newPasswordLabel = new JLabel("Nhập mật khẩu mới:");
        newPasswordLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        newPasswordLabel.setForeground(Color.decode("#090040"));
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(newPasswordLabel, gbc);

        newPasswordField = new RoundedTextField(20);
        newPasswordField.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 0;
        gbc.gridy = 6;
        contentPanel.add(newPasswordField, gbc);

        // Nhập lại mật khẩu
        JLabel confirmPasswordLabel = new JLabel("Nhập lại mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(Color.decode("#090040"));
        gbc.gridx = 0;
        gbc.gridy = 7;
        contentPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new RoundedTextField(20);
        confirmPasswordField.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 0;
        gbc.gridy = 8;
        contentPanel.add(confirmPasswordField, gbc);

        // Nút thực hiện yêu cầu
        submitButton = new RoundedButton("Thực hiện yêu cầu");
        submitButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        contentPanel.add(submitButton, gbc);

        add(contentPanel);
        setResizable(false);
    }

    // Getters để Controller dễ thao tác
    public String getEmail() {
        return emailField.getText();
    }

    public String getOtp() {
        return otpField.getText();
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    public void addSendOtpButtonListener(ActionListener listener) {
        sendOtpButton.addActionListener(listener);
    }

    public void addSubmitButtonListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}