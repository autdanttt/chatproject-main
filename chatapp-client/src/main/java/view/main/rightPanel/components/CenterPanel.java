package view.main.rightPanel.components;

import custom.RoundedPanel;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeature;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CenterPanel extends JPanel {
    private InfoOtherAndFeature infoOtherAndFeature;
    private MessagePanel messagePanel;
    @Inject
    public CenterPanel(InfoOtherAndFeature infoOtherAndFeature, MessagePanel messagePanel) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.messagePanel = messagePanel;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel mainChatPanel = new RoundedPanel(20, Color.WHITE, Color.decode("#E9E9E9"), 2);
        mainChatPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        mainChatPanel.add(infoOtherAndFeature, BorderLayout.NORTH);
        mainChatPanel.add(messagePanel, BorderLayout.CENTER);


        add(mainChatPanel, BorderLayout.CENTER);
    }
}
