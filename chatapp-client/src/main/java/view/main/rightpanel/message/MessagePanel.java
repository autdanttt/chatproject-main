package view.main.rightpanel.message;

import model.MessageType;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MessagePanel extends JPanel {
    private JPanel messageItemPanel;


    public MessagePanel() {
        setLayout(new BorderLayout());
        messageItemPanel = new JPanel();

        messageItemPanel.setLayout(new BoxLayout(messageItemPanel, BoxLayout.Y_AXIS));
        messageItemPanel.setBackground(new Color(240, 240, 240));
        JScrollPane messageScroll = new JScrollPane(messageItemPanel);
        add(messageScroll, BorderLayout.CENTER);


    }

    public JPanel getMessageItemPanel() {
        return messageItemPanel;
    }

    public void addMessage(String content, boolean isSentByMe, String time, MessageType messageType) {
        JPanel messageBubble = new JPanel(new BorderLayout());
        messageBubble.setMaximumSize(new Dimension(300, 100));

        JComponent messageComponent = null;

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
        } else if(messageType == MessageType.TEXT){
            JEditorPane messagePane = new JEditorPane("text/html", content);
            messagePane.setEditable(false);
            messagePane.setBackground(isSentByMe ? new Color(0, 122, 225) : new Color(230, 230, 230));
            messagePane.setForeground(Color.WHITE);
            messagePane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            messagePane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            messageComponent = messagePane;
        }else if(messageType == MessageType.IMAGE){
            File imageFile = new File(new File(System.getProperty("user.dir")).getParent() + "/uploads/" + content);
            System.out.println("File: " + imageFile.getAbsolutePath());
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(100,100 , Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                imageLabel.setOpaque(true);
                imageLabel.setBackground(isSentByMe ? new Color(0, 122, 225) : new Color(230, 230, 230));
                imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                messageComponent = imageLabel;
            } else {
                messageComponent = new JLabel("[Image not found: " + content + "]");
            }
        }

        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);

        if (isSentByMe) {
            messageBubble.add(messageComponent, BorderLayout.EAST);
        } else {
            messageBubble.add(messageComponent, BorderLayout.WEST);
        }

        messageBubble.add(timeLabel, BorderLayout.SOUTH);

        messageItemPanel.add(messageBubble);
        messageItemPanel.revalidate();
        messageItemPanel.repaint();

        JScrollPane messageScroll = (JScrollPane) getComponent(0);
        JScrollBar vertical = messageScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void clearMessages() {
        messageItemPanel.removeAll();
        messageItemPanel.revalidate();
        messageItemPanel.repaint();
    }
}
