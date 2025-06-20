package view.main;

import com.google.inject.Inject;
import view.main.chatlist.chatlist.ChatListController;
import view.main.chatlist.chatlist.ChatListPanel;
import view.main.rightpanel.RightPanel;
import view.main.search.search.SearchPanel;

import javax.swing.*;
import java.awt.*;

public class MainChatView extends JFrame {
    private final ChatListPanel chatListPanel;
    private final ChatListController chatListController;
    private final SearchPanel searchPanel;
    private final RightPanel rightPanel;

    @Inject
    public MainChatView(ChatListPanel chatListPanel, ChatListController chatListController,SearchPanel searchPanel, RightPanel rightPanel) {
        this.chatListPanel = chatListPanel;
        this.chatListController = chatListController;
        this.searchPanel = searchPanel;
        this.rightPanel = rightPanel;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(chatListPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);


        setSize(1000, 600);
        setVisible(true);
    }

    public void setUsername(String username) {
        setTitle( "Messager App - " + username);

    }
}
