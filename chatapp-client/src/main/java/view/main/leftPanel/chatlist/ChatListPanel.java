package view.main.leftPanel.chatlist;

import custom.ModernScrollBarUI;
import model.ChatItem;

import javax.swing.*;
import java.awt.*;

public class ChatListPanel extends JPanel {

    private DefaultListModel<ChatItem> chatListModel;
    private JList<ChatItem> chatList;

    public ChatListPanel() {
        setLayout(new BorderLayout());
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setCellRenderer((ListCellRenderer<? super ChatItem>) new ChatItemRenderer());
        chatList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane chatScrollPane = new JScrollPane(chatList);
        chatScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(chatScrollPane, BorderLayout.CENTER);
    }

    public DefaultListModel<ChatItem> getChatListModel() {
        return chatListModel;
    }

    public JList<ChatItem> getChatList() {
        return chatList;
    }
}
