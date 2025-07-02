package view.main.rightPanel.message;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import custom.ModernScrollBarUI;
import custom.RoundedPanel;
import event.LastMessageEvent;
import model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MessagePanel extends JPanel {
    private final Logger logger = LoggerFactory.getLogger(MessagePanel.class);
    private JPanel messageItemPanel;
    private EventBus eventBus;

    @Inject
    public MessagePanel(EventBus eventBus) {
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(700, 470));
        setBackground(Color.WHITE);

        messageItemPanel = new JPanel();
        messageItemPanel.setLayout(new BoxLayout(messageItemPanel, BoxLayout.Y_AXIS));
        messageItemPanel.setBackground(Color.WHITE);
        messageItemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JScrollPane scrollPane = new JScrollPane(messageItemPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> getLastVisibleMessageId());
        add(scrollPane);
    }

    public void addMessage(Long messageId, String content, String fromUserName, boolean isSentByMe, String time, MessageType messageType) {
        JPanel messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.X_AXIS));
        messageContainer.putClientProperty("messageId", messageId);
        messageContainer.setOpaque(false);

        JPanel messageBox = new RoundedPanel(15,
                isSentByMe ? Color.decode("#1F344D") : Color.WHITE,
                isSentByMe ? Color.decode("#426D9E") : Color.decode("#E9E9E9"),
                2);
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        int maxWidth = 300;
        messageBox.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));

        JLabel username = new JLabel(isSentByMe ? "Bạn" : fromUserName);
        username.setFont(new Font("Montserrat", Font.PLAIN, 10));
        username.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);

        JComponent messageComponent;

        if (messageType == MessageType.EMOJI) {
            File emojiFile = new File(new File(System.getProperty("user.dir")).getParent() + "/twemoji/" + content);
            if (emojiFile.exists()) {
                ImageIcon icon = new ImageIcon(emojiFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                messageComponent = new JLabel(new ImageIcon(scaled));
            } else {
                messageComponent = new JLabel("[Emoji not found]");
            }
        } else if (messageType == MessageType.TEXT) {
            JTextArea messageLabel = new JTextArea(content);
            messageLabel.setWrapStyleWord(true);
            messageLabel.setLineWrap(true);
            messageLabel.setEditable(false);
            messageLabel.setFocusable(false);
            messageLabel.setOpaque(false);
            messageLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
            messageLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);
            messageLabel.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
            messageComponent = messageLabel;
        } else if (messageType == MessageType.IMAGE) {
            File imageFile = new File(new File(System.getProperty("user.dir")).getParent() + "/uploads/" + content);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                messageComponent = new JLabel(new ImageIcon(scaled));
            } else {
                messageComponent = new JLabel("[Image not found]");
            }
        } else {
            messageComponent = new JLabel("[Unsupported type]");
        }

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Montserrat", Font.PLAIN, 10));
        timeLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);

        messageBox.add(username);
        messageBox.add(messageComponent);
        messageBox.add(Box.createVerticalStrut(4));
        messageBox.add(timeLabel);

        if (isSentByMe) {
            messageContainer.add(Box.createHorizontalGlue());
            messageContainer.add(messageBox);
        } else {
            messageContainer.add(messageBox);
            messageContainer.add(Box.createHorizontalGlue());
        }

        messageItemPanel.add(messageContainer);
        messageItemPanel.revalidate();
        messageItemPanel.repaint();

        JScrollPane scrollPane = (JScrollPane) getComponent(0);
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()));
    }

    public void clearMessages() {
        messageItemPanel.removeAll();
        messageItemPanel.revalidate();
        messageItemPanel.repaint();
    }

    public void getLastVisibleMessageId() {
        Rectangle visibleRect = messageItemPanel.getVisibleRect();
        Long lastVisibleId = null;
        for (Component comp : messageItemPanel.getComponents()) {
            if (comp instanceof JComponent jComp) {
                Rectangle compBounds = jComp.getBounds();
                if (visibleRect.intersects(compBounds)) {
                    Object messageId = jComp.getClientProperty("messageId");
                    if (messageId instanceof Long id) {
                        lastVisibleId = id;
                        logger.info("Last visible message id: {}", lastVisibleId);
                    }
                }
            }
        }
        if (lastVisibleId != null) {
            logger.info("Sending last visible message id: {}", lastVisibleId);
            eventBus.post(new LastMessageEvent(lastVisibleId));
        }
    }

    public JPanel getMessageItemPanel() {
        return messageItemPanel;
    }
}