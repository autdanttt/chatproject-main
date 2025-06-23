package view.main.rightPanel.message;

import custom.RoundedPanel;
import model.MessageType;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MessagePanel extends JPanel {
    private JPanel messageItemPanel;

    public MessagePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(500, 470));
        setBackground(Color.WHITE);

        messageItemPanel = new JPanel();
        messageItemPanel.setLayout(new BoxLayout(messageItemPanel, BoxLayout.Y_AXIS));
        messageItemPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(messageItemPanel);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getMessageItemPanel() {
        return messageItemPanel;
    }

    public void addMessage(String content, boolean isSentByMe, String time, MessageType messageType) {
        JPanel messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.X_AXIS));
        messageContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageContainer.setOpaque(false);
        messageContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel messageBox = new RoundedPanel(15, isSentByMe ? Color.decode("#1F344D") : Color.WHITE, isSentByMe ? Color.decode("#426D9E") : Color.decode("#E9E9E9"), 2);
        ;
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        messageBox.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

        JComponent messageComponent = null;

        JLabel username = new JLabel();
        username.setText(isSentByMe ? "Bạn" : "Other");
        username.setFont(new Font("Montserrat", Font.PLAIN, 10));
        username.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);

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

            int maxWidth = 250;
            FontMetrics fm = messageLabel.getFontMetrics(messageLabel.getFont());
            int lineCount = (int) Math.ceil(fm.stringWidth(content) / (double) maxWidth);
            int lineHeight = fm.getHeight();
            int preferredHeight = lineCount * lineHeight;
            int preferredWidth = preferredHeight <= 20 ? fm.stringWidth(content) + 2 : maxWidth;

            messageLabel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
            messageLabel.setMinimumSize(new Dimension(preferredWidth, preferredHeight));

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

        messageComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageBox.add(username);
        messageBox.add(messageComponent);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Montserrat", Font.PLAIN, 10));
        timeLabel.setForeground(isSentByMe ? Color.WHITE : Color.BLACK);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
}
