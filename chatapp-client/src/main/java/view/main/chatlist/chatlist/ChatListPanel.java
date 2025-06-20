package view.main.chatlist.chatlist;

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
        JScrollPane chatScroll = new JScrollPane(chatList);
        chatScroll.setPreferredSize(new Dimension(250, 0));


        add(chatScroll, BorderLayout.CENTER);
    }

    public DefaultListModel<ChatItem> getChatListModel() {
        return chatListModel;
    }

    public JList<ChatItem> getChatList() {
        return chatList;
    }
}
