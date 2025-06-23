package view.main.leftPanel.search;

import custom.RoundedTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {
    private final Logger logger = LoggerFactory.getLogger(SearchPanel.class);
    private JTextField searchTextField;
    private JButton searchButton;

    public SearchPanel() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(400, 100));
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel pn1 = new JPanel(new BorderLayout());
        pn1.setBackground(Color.WHITE);

        searchTextField = new RoundedTextField(20);
        searchTextField.setPreferredSize(new Dimension(250, 40));

        searchButton = new JButton();
        searchButton.setIcon(new ImageIcon("D:/chatproject-main/images/SEARCH.png"));
        searchButton.setPreferredSize(new Dimension(48, 48));
        searchButton.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 0));
        searchButton.setContentAreaFilled(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pn1.add(searchTextField, BorderLayout.CENTER);
        pn1.add(searchButton, BorderLayout.EAST);

        add(pn1, gbc);
    }

    public void addSearchButtonListener(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

}
