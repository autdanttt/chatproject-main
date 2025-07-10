package view.main.dialog.Rename;

import custom.RoundedButton;
import custom.RoundedPasswordField;
import custom.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RenameGroupDialog extends JDialog {
    private JTextField groupNameField;
    private JLabel avatarLabel;
    private JButton uploadButton;
    private JButton confirmButton, cancelButton;

    public RenameGroupDialog(JFrame parent, String currentGroupName) {
        super(parent, "Chỉnh sửa thông tin", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
//        setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setPreferredSize(new Dimension(600, 100));
        JLabel title = new JLabel("Chỉnh sửa thông tin".toUpperCase());
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

        JLabel currentNameLabel = new JLabel("Tên hiện tại: ");
        currentNameLabel.setFont(new Font("Montserrat", Font.BOLD, 15));

        JLabel currentName = new JLabel(currentGroupName);
        currentName.setFont(new Font("Montserrat", Font.BOLD, 14));
        currentName.setForeground(Color.BLACK);

        fullnamePanel.add(currentNameLabel, BorderLayout.NORTH);
        fullnamePanel.add(currentName, BorderLayout.CENTER);

        contentPanel.add(fullnamePanel, gbc);

        gbc.gridy++;
        JPanel passwordPanel = new JPanel(new BorderLayout(0, 5));
        passwordPanel.setBackground(Color.WHITE);

        JLabel newNameLabel = new JLabel("Tên mới: ");
        newNameLabel.setFont(new Font("Montserrat", Font.BOLD, 15));
        groupNameField = new RoundedTextField(20);
        groupNameField.setPreferredSize(new Dimension(220, 36));
        groupNameField.setFont(new Font("Montserrat", Font.PLAIN, 14));

        //passwordField = groupNameField
        //newNameLabel = newNameLabel
        //fullNameLabel = currentNameLabel
        //usernameField = currnetName
        //submitButton = confirmButton
        passwordPanel.add(newNameLabel, BorderLayout.NORTH);
        passwordPanel.add(groupNameField, BorderLayout.CENTER);

        contentPanel.add(passwordPanel, gbc);

        // ===== RIGHT SIDE (Avatar + Upload) =====
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


        // ===== Submit Button =====
        JPanel p3 = new JPanel(new BorderLayout());
        p3.setBackground(Color.WHITE);
        p3.setPreferredSize(new Dimension(600, 100));

        cancelButton = new RoundedButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(200, 44));

        confirmButton = new RoundedButton("Thực hiện yêu cầu");
        confirmButton.setPreferredSize(new Dimension(200, 44));

        int panelHeight = p3.getPreferredSize().height;
        int buttonHeight = confirmButton.getPreferredSize().height;
        int centerH = (panelHeight - buttonHeight) / 2 - 5;

        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, centerH));
        p4.setBackground(Color.WHITE);

        p4.add(cancelButton);
        p4.add(confirmButton);

        p3.add(p4, BorderLayout.CENTER);


        mainPanel.add(p1, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(p3, BorderLayout.SOUTH);

        setContentPane(mainPanel);

    }


    public JTextField getGroupNameField() {
        return groupNameField;
    }

    public JLabel getAvatarLabel() {
        return avatarLabel;
    }

    public JButton getUploadButton() {
        return uploadButton;
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }


    public String getNewGroupName() {
        return groupNameField.getText().trim();
    }

    public void addConfirmListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void addPhotoListener(ActionListener listener) {
        uploadButton.addActionListener(listener);
    }

}

