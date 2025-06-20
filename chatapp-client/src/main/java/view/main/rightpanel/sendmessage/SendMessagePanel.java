package view.main.rightpanel.sendmessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SendMessagePanel extends JPanel {
    private JButton emojiButton;
    private JButton imageButton;

    private JTextField textField;
    private JButton sendButton;
    private Consumer<File> emojiSelectedListener;
    private Consumer<File> imageSelectedListener;

    public SendMessagePanel() {
        setLayout(new BorderLayout(5, 2)); // spacing: 5px horizontal

        // Panel trái: emoji + image
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        emojiButton = new JButton("Emoji");
        emojiButton.addActionListener(e-> showEmojiPicker());
        imageButton = new JButton("Image");
        imageButton.addActionListener(e -> showFileChooser());
        leftPanel.add(emojiButton);
        leftPanel.add(imageButton);
        add(leftPanel, BorderLayout.WEST);

        // TextField ở giữa
        textField = new JTextField();
        add(textField, BorderLayout.CENTER);

        // Send button bên phải
        sendButton = new JButton("Send");
        add(sendButton, BorderLayout.EAST);
    }

    private void showFileChooser() {
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showOpenDialog(owner);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(imageSelectedListener != null) {
                imageSelectedListener.accept(file);
            }
        }
    }

    private void showEmojiPicker() {
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
        JDialog emojiDialog = new JDialog(owner,"Chọn Emoji", true);
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

    public void setEmojiSelectedListener(Consumer<File> emojiSelectedListener) {
        this.emojiSelectedListener = emojiSelectedListener;
    }

    public void setImageSelectedListener(Consumer<File> imageSelectedListener) {
        this.imageSelectedListener = imageSelectedListener;
    }

    public void addImageButtonListener(ActionListener listener) {
        imageButton.addActionListener(listener);
    }

    public void addSendButtonListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    public JTextField getTextField() {
        return textField;
    }
}
