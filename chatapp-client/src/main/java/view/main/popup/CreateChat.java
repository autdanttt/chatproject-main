package view.main.popup;

import custom.ModernScrollBarUI;
import custom.RoundedButton;
import custom.RoundedPanel;
import custom.RoundedTextField;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateChat extends JFrame {

    public CreateChat() {
        setTitle("Nhắn tin với người dùng");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout(5, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new GridLayout(2,1));
        JLabel title = new JLabel("Danh sách người dùng");
        title.setFont(new Font("Montserrat", Font.BOLD, 20));
        JTextField searchtxt = new RoundedTextField(20);
        headerPanel.setPreferredSize(new Dimension(500, 100));
        headerPanel.add(title);
        headerPanel.add(searchtxt);

        JPanel contentPanel = new RoundedPanel(20, Color.WHITE, Color.decode("#33333"), 2);
        DefaultListModel<User> userListModel = new DefaultListModel<>();
        userListModel.addElement(new User("Nguyễn Văn A", "token1"));
        userListModel.addElement(new User("Trần Thị B", "token2"));
        userListModel.addElement(new User("Lê Văn C", "token3"));
        userListModel.addElement(new User("Phạm Thị D", "token4"));
        userListModel.addElement(new User("Đỗ Văn E", "token5"));

        JList<User> userList = new JList<>(userListModel);
        userList.setCellRenderer(new UserItemRenderer());

        JScrollPane chatScrollPane = new JScrollPane(userList);
        chatScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setLayout(new BorderLayout(5, 0));
        footerPanel.setPreferredSize(new Dimension(500, 50));
        JButton addBtn = new RoundedButton("Thêm");

        footerPanel.add(addBtn, BorderLayout.CENTER);

        mainPanel.setBorder(new EmptyBorder(30 , 30, 30, 30));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setResizable(false);
        setVisible(true);
    }

    public DefaultListModel<User> userListModel() {
        return userListModel();
    }

    public JList<User> userList() {
        return userList();
    }
}
