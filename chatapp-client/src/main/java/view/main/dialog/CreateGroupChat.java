package view.main.dialog;

import custom.ModernScrollBarUI;
import custom.RoundedButton;
import custom.RoundedPanel;
import custom.RoundedTextField;
import lombok.Getter;
import model.UserOther;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreateGroupChat extends JDialog {
    @Getter
    private final DefaultListModel<UserOther> userListModel;
    @Getter
    private final JList<UserOther> userList;
    private final JButton addBtnGroup;
    @Getter
    private final JTextField nameGrouptxt;

    public CreateGroupChat(JFrame parent) {
        super(parent, "Nhắn tin với người dùng", true);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout(5, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(500, 100));
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

        nameGrouptxt = new RoundedTextField(20);
        nameGroupRow.add(nameGrouptxt, BorderLayout.CENTER);

        headerPanel.add(nameGroupRow);
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
        addBtnGroup = new RoundedButton("Thêm");

        footerPanel.add(addBtnGroup, BorderLayout.CENTER);

        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setResizable(false);
    }

    public void addBtnGroupListener(ActionListener actionListener) {
        addBtnGroup.addActionListener(actionListener);
    }
}