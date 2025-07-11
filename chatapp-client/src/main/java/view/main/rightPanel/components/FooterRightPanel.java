package view.main.rightPanel.components;

import custom.CreateButton;
import custom.RoundedTextField;
import lombok.Setter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FooterRightPanel extends JPanel {
    private JButton sendImage, sendBtn, sendEmoji;
    private final JTextField sendMessage;
    @Setter
    private Consumer<File> emojiSelectedListener;
    @Setter
    private Consumer<File> imageSelectedListener;
    private final String basePath = new File(System.getProperty("user.dir")).getParent();

    public FooterRightPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 11));
        setBackground(Color.WHITE);

        sendEmoji = new CreateButton(basePath + "/images/EMOJI.png");
        sendEmoji.addActionListener(e -> showEmojiPicker());

        sendImage = new CreateButton(basePath + "/images/SEND_IAMGE.png");
        sendImage.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sendImage.addActionListener(e -> showFileChooser());


        sendMessage = new RoundedTextField(35);
        sendMessage.setPreferredSize(new Dimension(400, 48));

        sendBtn = new CreateButton(basePath + "/images/SEND.png");
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(sendEmoji);
        add(sendImage);
        add(sendMessage);
        add(sendBtn);
    }

    private void showFileChooser() {
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Image files (*.jpg, *.jpeg, *.png, *.gif, *.bmp, *.jfif)",
                "jpg", "jpeg", "png", "gif", "bmp", "jfif"
        );
        fileChooser.setFileFilter(imageFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(owner);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String name = file.getName().toLowerCase();

            // Kiểm tra lại phần mở rộng để đảm bảo đúng định dạng ảnh
            if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                    || name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".jfif"))) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn file ảnh hợp lệ (.jpg, .jpeg, .png, .gif, .bmp, .jfif)",
                        "Lỗi định dạng",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi listener nếu file hợp lệ
            if (imageSelectedListener != null) {
                imageSelectedListener.accept(file);
            }
        }
    }

    private void showEmojiPicker() {
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
        JDialog emojiDialog = new JDialog(owner, "Chọn Emoji", true);
        emojiDialog.setLayout(new BorderLayout());
        emojiDialog.setSize(300, 400);

        JPanel emojiPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        List<String> selectedEmojis = new ArrayList<>();


        File twemojiDir = new File(basePath + "/twemoji");
        if (twemojiDir.exists() && twemojiDir.isDirectory()) {
            File[] files = twemojiDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (files != null) {
                for (File file : files) {
                    JLabel emojiLabel = new JLabel();
                    try {
                        String filePath = file.toURI().toString();
                        if (filePath != null) {
                            ImageIcon icon = new ImageIcon(new URL(filePath));
                            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                            emojiLabel.setIcon(new ImageIcon(img));
                        }
                    } catch (Exception e) {
                        emojiLabel.setText("Error");
                        System.out.println("Error loading image for " + file.getName() + " : " + e.getMessage());
                    }

                    emojiLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (emojiSelectedListener != null) {
                                emojiSelectedListener.accept(file);
                            }
                            emojiDialog.dispose();
                        }
                    });
                    emojiPanel.add(emojiLabel);
                }
            }
        } else {
            System.err.println("Twemoji directory not found at: " + twemojiDir.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Không tìm thấy thư mục twemoji: " + twemojiDir.getAbsolutePath());
        }


        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(e -> emojiDialog.dispose());
        emojiDialog.add(doneButton, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(emojiPanel);
        emojiDialog.add(scrollPane, BorderLayout.CENTER);


        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setVisible(true);
    }

    public void addSendButtonListener(ActionListener listener) {
        sendBtn.addActionListener(listener);
    }

    public JTextField getTextField() {
        return sendMessage;
    }

    public JTextField takeSendMessage() {
        return sendMessage;
    }
}
