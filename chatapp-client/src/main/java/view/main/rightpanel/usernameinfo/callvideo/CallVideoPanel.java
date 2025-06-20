package view.main.rightpanel.usernameinfo.callvideo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CallVideoPanel extends JPanel {
    private JButton videoButton;
    private JButton callButton;

    public CallVideoPanel() {
        setLayout(new FlowLayout());

        videoButton = new JButton("Video");
        callButton = new JButton("Call");

        add(callButton);
        add(videoButton);
    }

    public void addVideoButtonListener(ActionListener listener) {
        videoButton.addActionListener(listener);
    }

    public void addCallButtonListener(ActionListener listener) {
        callButton.addActionListener(listener);
    }
}
