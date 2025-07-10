package view.main.rightPanel;

import com.google.inject.Inject;
import controllers.HeaderRightController;
import view.main.rightPanel.components.CenterRightPanel;
import view.main.rightPanel.components.FooterRightPanel;
import view.main.rightPanel.components.HeaderRightPanel;
import view.main.rightPanel.message.MessageController;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.sendmessage.SendMessageController;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private final MessagePanel messagePanel;
    private final MessageController messageController;
    private final SendMessageController sendMessageController;
    private final HeaderRightController headerRightController;
    private HeaderRightPanel headerRightPanel;
    private CenterRightPanel centerRightPanel;
    private FooterRightPanel footerRightPanel;


    @Inject
    public RightPanel(MessagePanel messagePanel, MessageController messageController, SendMessageController sendMessageController, HeaderRightController headerRightController, HeaderRightPanel headerRightPanel, CenterRightPanel centerRightPanel, FooterRightPanel footerRightPanel) {
        this.messagePanel = messagePanel;
        this.messageController = messageController;
        this.sendMessageController = sendMessageController;
        this.headerRightController = headerRightController;
        this.headerRightPanel = headerRightPanel;
        this.centerRightPanel = centerRightPanel;
        this.footerRightPanel = footerRightPanel;

        setPreferredSize(new Dimension(600, 800));
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(headerRightPanel, BorderLayout.NORTH);
        add(centerRightPanel, BorderLayout.CENTER);
        add(footerRightPanel, BorderLayout.SOUTH);
    }

    public void reload() {
        revalidate();         // Cập nhật lại layout nếu cần
        repaint();            // Vẽ lại
    }
}
