package view.main.leftPanel;

import view.main.leftPanel.chatlist.ChatListPanel;
import view.main.leftPanel.search.SearchPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {
    private SearchPanel searchPanel;
    private ChatListPanel chatListPanel;

    @Inject
    public LeftPanel(SearchPanel searchPanel, ChatListPanel chatListPanel) {
        this.searchPanel = searchPanel;
        this.chatListPanel = chatListPanel;

        setLayout(new BorderLayout());

        JPanel leftMain = new JPanel();
        leftMain.setLayout(new BorderLayout());

        JLabel pnTitle = new JLabel("Chat List");
        pnTitle.setForeground(Color.BLACK);
        pnTitle.setFont(new Font("Montserrat", Font.BOLD, 20));
        pnTitle.setOpaque(true);
        pnTitle.setBackground(Color.WHITE);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        pnBottom.setPreferredSize(new Dimension(400, 80));
        pnBottom.setBackground(Color.WHITE);

        JButton addButton = new JButton();
        addButton.setIcon(new ImageIcon("D:/chatproject-main/images/ADD_USER.png"));
        addButton.setPreferredSize(new Dimension(48, 48));
        addButton.setBorder(BorderFactory.createEmptyBorder());
        addButton.setContentAreaFilled(false);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton addGroupButton = new JButton();
        addGroupButton.setIcon(new ImageIcon("D:/chatproject-main/images/ADD_GROUP_USER.png"));
        addGroupButton.setPreferredSize(new Dimension(48, 48));
        addGroupButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        addGroupButton.setContentAreaFilled(false);
        addGroupButton.setFocusPainted(false);
        addGroupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnBottom.add(addGroupButton);
        pnBottom.add(addButton);

        leftMain.add(pnTitle, BorderLayout.NORTH);
        leftMain.add(chatListPanel, BorderLayout.CENTER);
        leftMain.add(pnBottom, BorderLayout.SOUTH);

        add(searchPanel, BorderLayout.NORTH);
        add(leftMain, BorderLayout.CENTER);
    }
}
