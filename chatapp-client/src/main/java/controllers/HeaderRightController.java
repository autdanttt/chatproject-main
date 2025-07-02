package controllers;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import custom.RoundedImageUtil;
import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.UserToken;
import view.main.rightPanel.components.HeaderRightPanel;

import java.io.File;

public class HeaderRightController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(HeaderRightController.class);
    private static HeaderRightPanel headerRightPanel;
    private String basePath = new File(System.getProperty("user.dir")).getParent();

    @Inject
    public HeaderRightController(HeaderRightPanel headerRightPanel, EventBus eventBus) {
        this.headerRightPanel = headerRightPanel;
        eventBus.register(this);
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        setFullName(userToken.getFullName());
        setAvatarIcon(userToken.getAvatarUrl());
    }

    private void setFullName(String fullName) {
        headerRightPanel.getUserLabel().setText(fullName);
    }

    public void setAvatarIcon(String avatarUrl) {
        if (avatarUrl != null) {
            headerRightPanel.getAvatarIcon().setIcon(RoundedImageUtil.loadRoundedAvatarFromURL(avatarUrl, 40));
        }else {
            headerRightPanel.getAvatarIcon().setIcon(RoundedImageUtil.loadRoundedAvatarFromFile(basePath + "/images/DEFAULT_AVATAR.png", 40));
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
