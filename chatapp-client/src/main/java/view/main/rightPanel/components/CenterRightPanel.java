package view.main.rightPanel.components;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import custom.DefaultPanel;
import custom.RoundedPanel;
import event.ChatSelectedEvent;
import event.UserLogoutEvent;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeature;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeatureController;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CenterRightPanel extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final String DEFAULT_VIEW = "default";
    private final String CHAT_VIEW = "chat";

    private final InfoOtherAndFeature infoOtherAndFeature;
    private final MessagePanel messagePanel;
    private final InfoOtherAndFeatureController infoOtherAndFeatureController;
    private EventBus eventBus;

    @Inject
    public CenterRightPanel(InfoOtherAndFeature infoOtherAndFeature, MessagePanel messagePanel, InfoOtherAndFeatureController infoOtherAndFeatureController, EventBus eventBus) {
        this.infoOtherAndFeature = infoOtherAndFeature;
        this.messagePanel = messagePanel;
        this.infoOtherAndFeatureController = infoOtherAndFeatureController;
        this.eventBus = eventBus;
        eventBus.register(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(0, 10, 0, 10));

        // Default panel
        DefaultPanel defaultPanel = new DefaultPanel();

        // Chat panel
        JPanel mainChatPanel = new RoundedPanel(20, Color.WHITE, Color.decode("#E9E9E9"), 2);
        mainChatPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        mainChatPanel.setLayout(new BorderLayout());
        mainChatPanel.add(infoOtherAndFeature, BorderLayout.NORTH);
        mainChatPanel.add(messagePanel, BorderLayout.CENTER);

        // Add cards
        cardPanel.setBackground(Color.WHITE);
        cardPanel.add(defaultPanel, DEFAULT_VIEW);
        cardPanel.add(mainChatPanel, CHAT_VIEW);

        add(cardPanel, BorderLayout.CENTER);

        showDefaultPanel();
    }

    @Subscribe
    public void onUserLogout(UserLogoutEvent event) {
        SwingUtilities.invokeLater(this::showDefaultPanel);
    }

    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        SwingUtilities.invokeLater(this::showChatPanel);
    }


    public void showDefaultPanel() {
        cardLayout.show(cardPanel, DEFAULT_VIEW);
    }

    public void showChatPanel() {
        cardLayout.show(cardPanel, CHAT_VIEW);
    }
}
