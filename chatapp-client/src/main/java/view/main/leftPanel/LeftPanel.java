package view.main.leftPanel;

import view.main.leftPanel.chatlist.ChatListPanel;
import view.main.leftPanel.components.FooterPanel;
import view.main.leftPanel.search.SearchPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {
    private SearchPanel searchPanel;
    private ChatListPanel chatListPanel;
    private FooterPanel footerPanel;

    @Inject
    public LeftPanel(SearchPanel searchPanel, ChatListPanel chatListPanel, FooterPanel footerPanel) {
        this.searchPanel = searchPanel;
        this.chatListPanel = chatListPanel;
        this.footerPanel = footerPanel;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 800));
        JPanel leftMain = new JPanel();
        leftMain.setLayout(new BorderLayout());

        JLabel pnTitle = new JLabel("Chat List");
        pnTitle.setForeground(Color.BLACK);
        pnTitle.setFont(new Font("Montserrat", Font.BOLD, 20));
        pnTitle.setOpaque(true);
        pnTitle.setBackground(Color.WHITE);
        pnTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        leftMain.add(pnTitle, BorderLayout.NORTH);
        leftMain.add(chatListPanel, BorderLayout.CENTER);
        leftMain.add(footerPanel, BorderLayout.SOUTH);

        add(searchPanel, BorderLayout.NORTH);
        add(leftMain, BorderLayout.CENTER);
    }
}
