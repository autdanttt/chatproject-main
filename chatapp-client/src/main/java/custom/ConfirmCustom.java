package custom;

import javax.swing.*;
import java.awt.*;

public class ConfirmCustom extends JDialog {
    private boolean confirmed = false;

    public ConfirmCustom(Frame parent, String message, String title, ImageIcon icon, String cancel, String confirm) {
        super(parent, title, true);
        setLayout(new BorderLayout());
//        setUndecorated(true);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Message + Icon
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        messagePanel.setBackground(Color.WHITE);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            messagePanel.add(iconLabel);
        }

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
        messagePanel.add(messageLabel);
        contentPanel.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton confirmButton = new RoundedButton(confirm);
        JButton cancelButton = new RoundedButton(cancel);

        // Rounded corners
        confirmButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        // Action listeners
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean showConfirm() {
        setVisible(true);
        return confirmed;
    }

    public static boolean showConfirmDialog(Frame parent, String message, String title, ImageIcon icon, String cancel, String confirm) {
        ConfirmCustom dialog = new ConfirmCustom(parent, message, title, icon, cancel, confirm);
        return dialog.showConfirm();
    }
}