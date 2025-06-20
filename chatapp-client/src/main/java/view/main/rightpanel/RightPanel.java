package view.main.rightpanel;

import com.google.inject.Inject;
import view.main.rightpanel.message.MessageController;
import view.main.rightpanel.message.MessagePanel;
import view.main.rightpanel.sendmessage.SendMessageController;
import view.main.rightpanel.sendmessage.SendMessagePanel;
import view.main.rightpanel.usernameinfo.UsernameInfoPanel;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private final UsernameInfoPanel usernameInfoPanel;
    private final MessagePanel messagePanel;
    private final MessageController messageController;
    private final SendMessagePanel sendMessagePanel;
    private final SendMessageController sendMessageController;

    @Inject
    public RightPanel(UsernameInfoPanel usernameInfoPanel, MessagePanel messagePanel,MessageController messageController, SendMessagePanel sendMessagePanel, SendMessageController sendMessageController) {
        this.usernameInfoPanel = usernameInfoPanel;
        this.messagePanel = messagePanel;
        this.messageController = messageController;
        this.sendMessagePanel = sendMessagePanel;
        this.sendMessageController = sendMessageController;


        setLayout(new BorderLayout(5, 5));
        add(usernameInfoPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(sendMessagePanel, BorderLayout.SOUTH);

    }
}
