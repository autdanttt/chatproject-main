package controllers;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import custom.RoundedImageUtil;
import di.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.LoginView;
import view.login.TokenManager;
import view.main.UserToken;
import view.main.dialog.AvatarPopupMenu;
import view.main.dialog.EditProfileUser;
import view.main.rightPanel.components.HeaderRightPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class HeaderRightController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(HeaderRightController.class);
    private static HeaderRightPanel headerRightPanel;
    private String basePath = new File(System.getProperty("user.dir")).getParent();
    private EditProfileUser editProfileUser;

    private EditProfileUserController editProfileUserController;

    @Inject
    public HeaderRightController(HeaderRightPanel headerRightPanel, EditProfileUserController editProfileUserController, EventBus eventBus) {
        this.headerRightPanel = headerRightPanel;
        this.editProfileUserController = editProfileUserController;
        eventBus.register(this);

        initializeListeners();
    }

    private void initializeListeners() {
        headerRightPanel.getAvatarIcon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AvatarPopupMenu popupMenu = new AvatarPopupMenu(
                        editProfileEvent -> {
                            editProfileUser = new EditProfileUser((JFrame) SwingUtilities.getWindowAncestor(headerRightPanel));
                            editProfileUserController.setEditProfileUser(editProfileUser);
                            editProfileUser.setVisible(true);
                        },
                        logoutEvent -> {
                            JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(headerRightPanel);
                            int confirm = JOptionPane.showConfirmDialog(
                                    mainFrame,
                                    "Bạn có chắc chắn muốn đăng xuất không?",
                                    "Xác nhận đăng xuất",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE
                            );

                            if (confirm == JOptionPane.YES_OPTION) {
                                TokenManager.clear();
                                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(headerRightPanel);
                                currentFrame.dispose();
                                navigator.navigateTo("Login");
                            }
                        }
                );

                popupMenu.show(headerRightPanel.getAvatarIcon(), -160, 60);
            }
        });
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
        } else {
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
