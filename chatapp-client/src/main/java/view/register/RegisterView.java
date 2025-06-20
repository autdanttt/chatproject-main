package view.register;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame {
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton sendButton;
    private JButton cancelButton;
    private String username;

    public RegisterView() {
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        if(username != null) {
            usernameField.setText(username);
        }
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        sendButton = new JButton("Send");
        panel.add(sendButton);
        cancelButton = new JButton("Cancel");
        panel.add(cancelButton);

        add(panel);
        setLocationRelativeTo(null);
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
