package view;

import controller.ChatController;
import model.ChatItem;

import javax.swing.*;
import java.awt.*;

public class ChatView {
    private JFrame frame;
    private JTextField searchInput, messageInput;
    private JButton searchButton, sendButton, emojiButton, attachButton;
    private JList<ChatItem> chatList;
    private DefaultListModel<ChatItem> chatListModel;
    private JPanel chatPanel;
    private JLabel nameUserInput;
    private ChatController chatController;
    private JPanel messagesPanel;

    public ChatView(String username) {
        frame = new JFrame("Zola");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchInput = new JTextField(15);
        searchButton = new JButton("Search");
        nameUserInput = new JLabel(username);
        searchPanel.add(searchInput, BorderLayout.WEST);
        searchPanel.add(searchButton, BorderLayout.CENTER);
        searchPanel.add(nameUserInput, BorderLayout.EAST);

        // Chat list
        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setCellRenderer((ListCellRenderer<? super ChatItem>) new ChatItemRenderer());
        JScrollPane chatScroll = new JScrollPane(chatList);
        chatScroll.setPreferredSize(new Dimension(250, 0));

        // Chat panel with messages
        chatPanel = new JPanel(new BorderLayout());
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(240, 240, 240));
        JScrollPane messageScroll = new JScrollPane(messagesPanel);
        chatPanel.add(messageScroll, BorderLayout.CENTER); // Thay chatScroll bằng messageScroll

        // Split pane to show chat list and messages side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, chatPanel);
        splitPane.setDividerLocation(250);

        // Input panel
        messageInput = new JTextField();
        sendButton = new JButton("Send");
        emojiButton = new JButton("😊");
        attachButton = new JButton("📎");

        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(emojiButton);
        buttonPanel.add(attachButton);
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.WEST);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Main layout
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

//    public void addMessage(String )


    public JFrame getFrame() { return frame; }
    public JTextField getSearchInput() { return searchInput; }
    public JButton getSearchButton() { return searchButton; }
    public JList<ChatItem> getChatList() { return chatList; }
    public DefaultListModel<ChatItem> getChatListModel() { return chatListModel; }
    public JPanel getChatPanel() { return chatPanel; }
    public JTextField getMessageInput() { return messageInput; }
    public JButton getSendButton() { return sendButton; }

    public JButton getEmojiButton() {
        return emojiButton;
    }

    public JButton getAttachButton() {
        return attachButton;
    }

    public JLabel getNameUserInput() {
        return nameUserInput;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public JPanel getMessagesPanel() {
        return messagesPanel;
    }

    public void clearMessages() {
        messagesPanel.removeAll();
        messagesPanel.revalidate();
        messagesPanel.repaint();

    }

    public void addMessage(String content, Long fromUserId, boolean isSentByMe, String time) {
        JPanel messageBubble = new JPanel(new BorderLayout());
        messageBubble.setMaximumSize(new Dimension(300, 100));
        JLabel messageLabel = new JLabel("<html>" + content.replace("\n", "<br>") + "</html>");
        JLabel timeLabel = new JLabel(time);

        if (isSentByMe) {
            messageBubble.setBackground(new Color(0, 122, 255)); // Màu xanh cho tin nhắn của mình
            messageBubble.setForeground(Color.WHITE);
            messageBubble.add(messageLabel, BorderLayout.EAST);
            messageBubble.add(timeLabel, BorderLayout.SOUTH);
            messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        } else {
            messageBubble.setBackground(new Color(230, 230, 230)); // Màu xám cho tin nhắn người khác
            messageBubble.add(messageLabel, BorderLayout.WEST);
            messageBubble.add(timeLabel, BorderLayout.SOUTH);
            messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        messagesPanel.add(messageBubble);
        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Cuộn đến cuối
        JScrollPane messageScroll = (JScrollPane) chatPanel.getComponent(0); // Lấy JScrollPane chứa messagesPanel
        JScrollBar vertical = messageScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
}

