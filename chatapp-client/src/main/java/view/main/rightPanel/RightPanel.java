package view.main.rightPanel;

import com.google.inject.Inject;
import view.main.rightPanel.components.CenterPanel;
import view.main.rightPanel.components.FooterPanel;
import view.main.rightPanel.components.HeaderPanel;
import view.main.rightPanel.message.MessageController;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.sendmessage.SendMessageController;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeature;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private final InfoOtherAndFeature usernameInfoPanel;
    private final MessagePanel messagePanel;
    private final MessageController messageController;
    private final SendMessageController sendMessageController;
    private HeaderPanel headerPanel;
    private CenterPanel centerPanel;
    private FooterPanel footerPanel;

    @Inject
    public RightPanel(InfoOtherAndFeature usernameInfoPanel, MessagePanel messagePanel, MessageController messageController, SendMessageController sendMessageController, HeaderPanel headerPanel, CenterPanel centerPanel, FooterPanel footerPanel) {
        this.usernameInfoPanel = usernameInfoPanel;
        this.messagePanel = messagePanel;
        this.messageController = messageController;
        this.sendMessageController = sendMessageController;
        this.headerPanel = headerPanel;
        this.centerPanel = centerPanel;
        this.footerPanel = footerPanel;

        setPreferredSize(new Dimension(700, 800));
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setUserName(String userName) {
        headerPanel.setUserName(userName);
    }
}
