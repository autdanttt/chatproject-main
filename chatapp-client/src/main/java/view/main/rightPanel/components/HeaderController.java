package view.main.rightPanel.components;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.UserToken;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class HeaderController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(HeaderController.class);
    private static HeaderPanel headerPanel;
    private String avatarUrl;

    @Inject
    public HeaderController(HeaderPanel headerPanel,EventBus eventBus) {
        this.headerPanel = headerPanel;
        eventBus.register(this);
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        log.info("onJwtToken: " + userToken.getJwtToken());
        this.avatarUrl = userToken.getAvatarUrl();
        setAvatarIcon(userToken.getAvatarUrl());
    }
    public void setAvatarIcon(String avatarUrl) {
        log.info("Setting avatar icon to " + avatarUrl);
        try {
            BufferedImage originalImage = ImageIO.read(new URL(avatarUrl));
            // Resize ảnh về đúng kích thước label (40x40)
            Image scaledImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

            ImageIcon icon = new ImageIcon(scaledImage);

            headerPanel.getAvatarIcon().setIcon(icon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void setupDependencies() {

    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
