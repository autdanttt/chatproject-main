package view.main.rightPanel.message;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import custom.ModernScrollBarUI;
import custom.RoundedPanel;
import event.LastMessageEvent;
import model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MessagePanel extends JPanel {
    private final Logger logger = LoggerFactory.getLogger(MessagePanel.class);
    private JPanel messageItemPanel;
    private EventBus eventBus;

    @Inject
    public MessagePanel(EventBus eventBus) {
        this.eventBus = eventBus;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(600, 470));
        setBackground(Color.WHITE);

        messageItemPanel = new JPanel();
        messageItemPanel.setLayout(new BoxLayout(messageItemPanel, BoxLayout.Y_AXIS));
        messageItemPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(messageItemPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> getLastVisibleMessageId());
        add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getMessageItemPanel() {
        return messageItemPanel;
    }

    public void addMessage(Long messageId,String content,String fromFullName ,boolean isSentByMe, String time, MessageType messageType) {
        JPanel messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.X_AXIS));
        messageContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageContainer.setOpaque(false);

        messageContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Gan messageId vao component
        messageContainer.putClientProperty("messageId", messageId);

        JPanel messageBox = new RoundedPanel(15, isSentByMe ? Color.decode("#1F344D") : Color.WHITE, isSentByMe ? Color.decode("#426D9E") : Color.decode("#E9E9E9"), 2);
        ;
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        //messageBox.setMaximumSize(new Dimension(300, 250));

        JComponent messageComponent = null;

        JLabel username = new JLabel();
        username.setText(isSentByMe ? "Bạn" : fromFullName);
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
//            logger.info("StringWidth content: " + fm.stringWidth(content) );
//            logger.info("lineCount: " + lineCount);
//            logger.info("lineHeight: " + lineHeight);
            int preferredHeight = lineCount * lineHeight;
            int preferredWidth = preferredHeight <= 20 ? fm.stringWidth(content) + 2 : maxWidth;
//            logger.info("preferredWidth: " + preferredWidth);
//            logger.info("preferredHeight: " + preferredHeight);

            messageLabel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
            messageLabel.setMinimumSize(new Dimension(preferredWidth, preferredHeight));

            messageComponent = messageLabel;
        } else if (messageType == MessageType.IMAGE) {
            try {
                BufferedImage originalImage = ImageIO.read(new URL(content));
                if (originalImage != null) {
                    int displayWidth = 160;
                    int displayHeight = 160;
                    Image scaledImage = originalImage.getScaledInstance(displayWidth, displayHeight, Image.SCALE_SMOOTH);
                    //messageComponent = new JLabel(new ImageIcon(scaledImage));

                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel imageLabel = new JLabel(scaledIcon);

// Sự kiện click vào ảnh
                    imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ImageIcon fullImageIcon = new ImageIcon(originalImage);
                            showFullImageDialog(fullImageIcon); // Hiển thị ảnh lớn
                        }
                    });

                    messageComponent = imageLabel;


                } else {
                    messageComponent = new JLabel("[Image not available]");
                }
            } catch (IOException e) {
                logger.error("Error loading image from URL: {}", content, e);
                messageComponent = new JLabel("[Image load error]");
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

        // Tính kích thước cho messageBox dựa trên các thành phần bên trong
        int boxPaddingHorizontal = 12 + 12; // Padding left + right
        int boxPaddingVertical = 8 + 8; // Padding top + bottom
        int messageComponentWidth = messageComponent.getPreferredSize().width;
        int timeLabelWidth = timeLabel.getPreferredSize().width;
        int nameWidth = username.getPreferredSize().width;


        int boxWidth = Math.max(messageComponentWidth,Math.max(timeLabelWidth,nameWidth)) + boxPaddingHorizontal;
        int usernameHeight = username.getPreferredSize().height;
        int messageComponentHeight = messageComponent.getPreferredSize().height;
        int timeLabelHeight = timeLabel.getPreferredSize().height;

        int boxHeight = usernameHeight + 3 + messageComponentHeight + 4 + timeLabelHeight + boxPaddingVertical;
//        logger.info("messageComponentWidth: " + messageComponentWidth);
//        logger.info("timeLabelWidth: " + timeLabelWidth);
//        logger.info("boxWidth: " + boxWidth);
//        logger.info("boxHeight: " + boxHeight);
        messageBox.setPreferredSize(new Dimension(boxWidth, boxHeight));
        messageBox.setMinimumSize(new Dimension(boxWidth, boxHeight));
        messageBox.setMaximumSize(new Dimension(300, boxHeight)); // Giới hạn chiều rộng tối đa là 300

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
            if(comp instanceof JComponent jComp){
                Rectangle compBounds = jComp.getBounds();
                if(visibleRect.intersects(compBounds)){
                    Object messageId = jComp.getClientProperty("messageId");
                    if(messageId instanceof  Long id){
                        lastVisibleId = (Long) id;
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
    private void showFullImageDialog(ImageIcon imageIcon) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Xem ảnh");
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);

        JLabel imageLabel = new JLabel(imageIcon);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        dialog.add(scrollPane);

        dialog.setModal(true); // chặn thao tác bên ngoài khi đang xem ảnh
        dialog.setVisible(true);
    }

}
