package view.main.leftPanel.components;

import custom.CreateButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FooterPanel extends JPanel {
    private JButton addButton, deleteButton, addGroupButton;

    public FooterPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        setPreferredSize(new Dimension(400, 80));
        setBackground(Color.WHITE);

        deleteButton = new CreateButton("D:/chatproject-main/images/DELETE_CHAT.png");
        addButton = new CreateButton("D:/chatproject-main/images/ADD_USER.png");
        addGroupButton = new CreateButton("D:/chatproject-main/images/ADD_GROUP_USER.png");

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
