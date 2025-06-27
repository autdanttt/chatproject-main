package view.main.dialog;

import custom.ModernScrollBarUI;
import custom.RoundedButton;
import custom.RoundedPanel;
import custom.RoundedTextField;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateGroupChat extends JFrame {
    public CreateGroupChat() {
        setTitle("Nhắn tin với ngóm");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout(5, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(500, 150));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Danh sách người dùng");
        title.setFont(new Font("Montserrat", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(10));

        JPanel nameGroupRow = new JPanel(new BorderLayout());
        nameGroupRow.setBackground(Color.WHITE);
        nameGroupRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameGrouptxt = new RoundedTextField(20);
        nameGroupRow.add(nameGrouptxt, BorderLayout.CENTER);

        JLabel selectionLabel = new JLabel("Đang chọn:");
        selectionLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        selectionLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        nameGroupRow.add(selectionLabel, BorderLayout.EAST);

        headerPanel.add(nameGroupRow);
        headerPanel.add(Box.createVerticalStrut(10));

        JTextField searchtxt = new RoundedTextField(20);
        searchtxt.setText("Tìm kiếm người dùng...");
        searchtxt.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setResizable(false);
    }
}
