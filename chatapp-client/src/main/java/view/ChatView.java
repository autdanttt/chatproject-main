package view;

import controller.ChatController;
import model.ChatItem;
import model.MessageType;
import utility.EmojiUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatView {
    private JFrame frame;
    private JTextField searchInput ;
    private JTextPane messageInput;
    private JButton searchButton, sendButton, emojiButton, attachButton;
    private JList<ChatItem> chatList;
    private DefaultListModel<ChatItem> chatListModel;
    private JPanel chatPanel;
    private JLabel nameUserInput;
    private ChatController chatController;
    private JPanel messagesPanel;
    private Consumer<File> emojiSelectedListener;

    public ChatView(String username, ChatController chatController) {
        this.chatController = chatController;

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

        messageInput = new JTextPane();
        messageInput.setEditable(true);
//
////// Cấu hình HTMLEditorKit và StyleSheet
////        HTMLEditorKit kit = new HTMLEditorKit();
////        StyleSheet styleSheet = kit.getStyleSheet();
////        styleSheet.addRule("img { vertical-align: middle; }");
////        messageInput.setEditorKit(kit);
//
//// Tạo Document mới đúng chuẩn từ kit đã gán
//        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
//        messageInput.setDocument(doc);
//
//// Khởi tạo HTML rỗng với body hợp lệ
//        messageInput.setText("<html><body></body></html>");

        sendButton = new JButton("Send");

        emojiButton = new JButton("😊");

        attachButton = new JButton("📎");


        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(emojiButton);

        emojiButton.addActionListener(e -> showEmojiPicker());
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
    public void setEmojiSelectedListener(Consumer<File> emojiSelectedListener) {
        this.emojiSelectedListener = emojiSelectedListener;
    }


    private void showEmojiPicker() {
        JDialog emojiDialog = new JDialog(frame, "Chọn Emoji", true);
        emojiDialog.setLayout(new BorderLayout());
        emojiDialog.setSize(300, 400);

        JPanel emojiPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        List<String> selectedEmojis = new ArrayList<>();


//        String basePath = System.getProperty("user.dir");
//        System.out.println("Base path: " + basePath);
//
//        File twemojiDir = new File(basePath , "twemoji");
        String basePath = new File(System.getProperty("user.dir")).getParent() + "/twemoji";
        File twemojiDir = new File(basePath);
        if(twemojiDir.exists() && twemojiDir.isDirectory()) {
            File[] files = twemojiDir.listFiles((dir, name) -> name.endsWith(".png"));
            if(files != null) {
                for (File file : files) {
                    JLabel emojiLabel = new JLabel();
                    try{
                        String filePath = file.toURI().toString();
                        if(filePath != null){
                            ImageIcon icon = new ImageIcon(new URL(filePath));
                            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                            emojiLabel.setIcon(new ImageIcon(img));
                        }
                    }catch (Exception e) {
                        emojiLabel.setText("Error");
                        System.out.println("Error loading image for " + file.getName() + " : " + e.getMessage());
                    }


                    emojiLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if(emojiSelectedListener != null) {
                                emojiSelectedListener.accept(file);
                            }
                            emojiDialog.dispose();

                        }
                    });
                    emojiPanel.add(emojiLabel);
                }
            }
        }else {
            System.err.println("Twemoji directory not found at: " + twemojiDir.getAbsolutePath());
            JOptionPane.showMessageDialog(frame, "Không tìm thấy thư mục twemoji: " + twemojiDir.getAbsolutePath());
        }


        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(e -> emojiDialog.dispose());
        emojiDialog.add(doneButton, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(emojiPanel);
        emojiDialog.add(scrollPane, BorderLayout.CENTER);


        emojiDialog.setLocationRelativeTo(frame);
        emojiDialog.setVisible(true);
    }



    public JFrame getFrame() { return frame; }
    public JTextField getSearchInput() { return searchInput; }
    public JButton getSearchButton() { return searchButton; }
    public JList<ChatItem> getChatList() { return chatList; }
    public DefaultListModel<ChatItem> getChatListModel() { return chatListModel; }
    public JPanel getChatPanel() { return chatPanel; }

    public JTextPane getMessageInput() {
        return messageInput;
    }

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

//    public void addMessage(String content, Long fromUserId, boolean isSentByMe, String time, MessageType messageType) {
//
//        JPanel messageBubble = new JPanel(new BorderLayout());
//        messageBubble.setMaximumSize(new Dimension(300, 100));
//
//        JEditorPane messagePane = new JEditorPane("text/html", content);
//
//
//        if(messageType.equals(MessageType.TEXT)) {
//        }else {
//            messagePane.setEditable(false);
//            messagePane.setBackground(isSentByMe ? new Color(0, 122, 225) : new Color(230, 230, 230));
//            messagePane.setForeground(Color.WHITE);
//            messagePane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
//            messagePane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//        }
//
//        JLabel timeLabel = new JLabel(time);
//        timeLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);
//
//        if(isSentByMe){
//            messageBubble.add(messagePane, BorderLayout.EAST);
//        }else {
//            messageBubble.add(messagePane, BorderLayout.WEST);
//        }
//
//        messageBubble.add(timeLabel, BorderLayout.SOUTH);
//
//        messagesPanel.add(messageBubble);
//        messagesPanel.revalidate();
//        messagesPanel.repaint();
//
//
//        JScrollPane messageScroll = (JScrollPane) chatPanel.getComponent(0);
//        JScrollBar vertical = messageScroll.getVerticalScrollBar();
//        vertical.setValue(vertical.getMaximum());
//    }
public void addMessage(String content, Long fromUserId, boolean isSentByMe, String time, MessageType messageType) {
    JPanel messageBubble = new JPanel(new BorderLayout());
    messageBubble.setMaximumSize(new Dimension(300, 100));

    JComponent messageComponent;

    if (messageType == MessageType.EMOJI) {
        // Emoji path đã được gửi dưới dạng filename như "smile.png"
//        File emojiFile = new File(System.getProperty("user.dir") + "/twemoji/" + content);
        File emojiFile = new File(new File(System.getProperty("user.dir")).getParent() + "/twemoji/" + content);
        System.out.println("File: " + emojiFile.getAbsolutePath());
        if (emojiFile.exists()) {
            ImageIcon icon = new ImageIcon(emojiFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel emojiLabel = new JLabel(new ImageIcon(scaled));
            emojiLabel.setOpaque(true);
            emojiLabel.setBackground(isSentByMe ? new Color(0, 122, 225) : new Color(230, 230, 230));
            emojiLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            messageComponent = emojiLabel;
        } else {
            messageComponent = new JLabel("[Emoji not found: " + content + "]");
        }
    } else {
        JEditorPane messagePane = new JEditorPane("text/html", content);
        messagePane.setEditable(false);
        messagePane.setBackground(isSentByMe ? new Color(0, 122, 225) : new Color(230, 230, 230));
        messagePane.setForeground(Color.WHITE);
        messagePane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        messagePane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageComponent = messagePane;
    }

    JLabel timeLabel = new JLabel(time);
    timeLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);

    if (isSentByMe) {
        messageBubble.add(messageComponent, BorderLayout.EAST);
    } else {
        messageBubble.add(messageComponent, BorderLayout.WEST);
    }

    messageBubble.add(timeLabel, BorderLayout.SOUTH);

    messagesPanel.add(messageBubble);
    messagesPanel.revalidate();
    messagesPanel.repaint();

    JScrollPane messageScroll = (JScrollPane) chatPanel.getComponent(0);
    JScrollBar vertical = messageScroll.getVerticalScrollBar();
    vertical.setValue(vertical.getMaximum());
    }
}

