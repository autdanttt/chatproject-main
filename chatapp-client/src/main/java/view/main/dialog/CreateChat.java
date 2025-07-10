package view.main.dialog;

import custom.ModernScrollBarUI;
import custom.RoundedButton;
import custom.RoundedPanel;
import custom.RoundedTextField;
import model.UserOther;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreateChat extends JDialog {
    private JList<UserOther> userList;
    private DefaultListModel<UserOther> userListModel;
    private JButton addBtn;

    public CreateChat(JFrame parent) {
        super(parent, "Nhắn tin với người dùng", true);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout(5, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new GridLayout(2, 1));
        JLabel title = new JLabel("Danh sách người dùng");
        title.setFont(new Font("Montserrat", Font.BOLD, 20));
        headerPanel.add(title);

        JPanel contentPanel = new RoundedPanel(20, Color.WHITE, Color.decode("#33333"), 2);
        userListModel = new DefaultListModel<>();

        userList = new JList<>(userListModel);
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
        addBtn = new RoundedButton("Thêm");

        footerPanel.add(addBtn, BorderLayout.CENTER);

        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setResizable(false);
    }

    public JList<UserOther> getUserList() {
        return userList;
    }

    public DefaultListModel<UserOther> getUserListModel() {
        return userListModel;
    }

    public void handlerAddChat(ActionListener e) {
        addBtn.addActionListener(e);
    }

    public void selectItemListUser(ListSelectionListener e) {
        userList.addListSelectionListener(e);
    }
}
