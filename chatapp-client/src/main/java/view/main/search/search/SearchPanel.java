package view.main.search.search;

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
        setLayout(new BorderLayout());  // ✅ Cần dòng này

        searchTextField = new JTextField(15);
        searchButton = new JButton("Search");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(searchTextField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);
    }

    public void addSearchButtonListener(ActionListener actionListener) {
        searchButton.addActionListener(actionListener);
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

}
