package view.main;

import com.google.inject.Inject;
import controllers.FooterLeftController;
import view.main.leftPanel.chatlist.ChatListController;
import view.main.leftPanel.chatlist.ChatListPanel;
import view.main.leftPanel.LeftPanel;
import view.main.rightPanel.RightPanel;
import view.main.leftPanel.search.SearchPanel;

import javax.swing.*;
import java.awt.*;

public class MainChatView extends JFrame {
    private final ChatListPanel chatListPanel;
    private final ChatListController chatListController;
    private final SearchPanel searchPanel;
    private final RightPanel rightPanel;
    private LeftPanel leftPanel;

    @Inject
    public MainChatView(ChatListPanel chatListPanel, ChatListController chatListController, SearchPanel searchPanel, LeftPanel leftPanel, RightPanel rightPanel, FooterLeftController footerLeftController) {
        this.chatListPanel = chatListPanel;
        this.chatListController = chatListController;
        this.searchPanel = searchPanel;
        this.rightPanel = rightPanel;
        this.leftPanel = leftPanel;

        initUI();
    }

    private void initUI() {
        setTitle("Chat App");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setResizable(false);
    }

}
