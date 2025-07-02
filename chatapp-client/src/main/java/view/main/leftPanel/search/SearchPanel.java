package view.main.leftPanel.search;

import custom.CreateButton;
import custom.RoundedTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class SearchPanel extends JPanel {
    private final Logger logger = LoggerFactory.getLogger(SearchPanel.class);
    private JTextField searchTextField;
    private JButton searchButton;
    private String basePath = new File(System.getProperty("user.dir")).getParent();

    public SearchPanel() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(300, 100));
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel pn1 = new JPanel(new BorderLayout());
        pn1.setBackground(Color.WHITE);

        searchTextField = new RoundedTextField(15);
        searchTextField.setPreferredSize(new Dimension(200, 35));

        searchButton = new CreateButton(basePath + "/images/SEARCH.png");

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
