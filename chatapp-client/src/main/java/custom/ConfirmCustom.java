package custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmCustom extends JDialog {
    private boolean confirmed = false;

    public ConfirmCustom(Frame parent, String message, String title, ImageIcon icon, String cacel, String confirm) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Message + Icon
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        messagePanel.setBackground(Color.WHITE);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            messagePanel.add(iconLabel);
        }

        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Montserrat", Font.BOLD, 25));
        messagePanel.add(messageLabel);

        contentPanel.add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton confirmButton = new RoundedButton(confirm);

        JButton cancelButton = new RoundedButton(cacel);

        // Rounded corners
        confirmButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        // Action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
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


    public static boolean showConfirmDialog(Frame parent, String message, String title, ImageIcon icon , String cacel, String confirm) {
        ConfirmCustom dialog = new ConfirmCustom(parent, message, title, icon, cacel, confirm);
        return dialog.showConfirm();
    }
}