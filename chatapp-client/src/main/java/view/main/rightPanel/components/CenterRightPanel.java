package view.main.rightPanel.components;

import custom.RoundedPanel;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeature;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeatureController;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CenterRightPanel extends JPanel {
    private InfoOtherAndFeature infoOtherAndFeature;
    private MessagePanel messagePanel;

    private InfoOtherAndFeatureController infoOtherAndFeatureController;
    @Inject
    public CenterRightPanel(InfoOtherAndFeature infoOtherAndFeature, MessagePanel messagePanel, InfoOtherAndFeatureController infoOtherAndFeatureController) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.messagePanel = messagePanel;
        this.infoOtherAndFeatureController = infoOtherAndFeatureController;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel mainChatPanel = new RoundedPanel(20, Color.WHITE, Color.decode("#E9E9E9"), 2);
        mainChatPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        mainChatPanel.add(infoOtherAndFeature, BorderLayout.NORTH);
        mainChatPanel.add(messagePanel, BorderLayout.CENTER);

        add(mainChatPanel, BorderLayout.CENTER);
    }
}
