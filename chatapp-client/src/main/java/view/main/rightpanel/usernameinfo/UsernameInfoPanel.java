package view.main.rightpanel.usernameinfo;

import com.google.inject.Inject;
import view.main.rightpanel.usernameinfo.callvideo.CallVideoController;
import view.main.rightpanel.usernameinfo.callvideo.CallVideoPanel;
import view.main.rightpanel.usernameinfo.usernamestatus.UsernameStatusController;
import view.main.rightpanel.usernameinfo.usernamestatus.UsernameStatusPanel;

import javax.swing.*;
import java.awt.*;

public class UsernameInfoPanel extends JPanel {
    private final CallVideoPanel callVideoPanel;
    private final CallVideoController callVideoController;
    private final UsernameStatusPanel usernameStatusPanel;
    private final UsernameStatusController usernameStatusController;

    @Inject
    public UsernameInfoPanel(CallVideoPanel callVideoPanel,CallVideoController callVideoController, UsernameStatusPanel usernameStatusPanel, UsernameStatusController usernameStatusController) {
        this.callVideoPanel = callVideoPanel;
        this.callVideoController = callVideoController;
        this.usernameStatusPanel = usernameStatusPanel;
        this.usernameStatusController = usernameStatusController;

        setLayout(new BorderLayout());

        add(callVideoPanel, BorderLayout.EAST);
        add(usernameStatusPanel, BorderLayout.WEST);

    }
}
