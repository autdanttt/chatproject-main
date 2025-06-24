package view.main.leftPanel.components;

import custom.CreateButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class FooterPanel extends JPanel {
    private JButton addButton, deleteButton, addGroupButton;
    private String basePath = new File(System.getProperty("user.dir")).getParent();

    public FooterPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        setPreferredSize(new Dimension(400, 80));
        setBackground(Color.WHITE);

        deleteButton = new CreateButton(basePath + "/images/DELETE_CHAT.png");
        addButton = new CreateButton(basePath + "/images/ADD_USER.png");
        addGroupButton = new CreateButton(basePath + "/images/ADD_GROUP_USER.png");

        add(deleteButton);
        add(addGroupButton);
        add(addButton);
    }

    public void addChatListener(ActionListener actionListener) {
        addButton.addActionListener(actionListener);
    }

    public void deleteChatListener(ActionListener actionListener) {
        deleteButton.addActionListener(actionListener);
    }

    public void addGroupListener(ActionListener actionListener) {
        addGroupButton.addActionListener(actionListener);
    }
}
