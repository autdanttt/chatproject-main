package controllers;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import di.BaseController;
import event.UserProfileUpdatedEvent;
import model.UpdateUserRequest;
import model.UserOther;
import view.login.TokenManager;
import view.main.UserToken;
import view.main.dialog.EditProfileUser.EditProfileImpl;
import view.main.dialog.EditProfileUser.EditProfileUser;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class EditProfileUserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EditProfileUserController.class);
    private EditProfileUser editProfileUser;
    private final EventBus eventBus;
    private File selectedImageFile = null;
    private EditProfileImpl editProfileImpl;
    private UserToken userToken;

    @Inject
    public EditProfileUserController(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
        this.editProfileImpl = new EditProfileImpl();
    }

    public void setEditProfileUser(EditProfileUser editProfileUser) {
        this.editProfileUser = editProfileUser;
        String fullName = TokenManager.getFullName();
        String avatarUrl = TokenManager.getAvatarUrl();
        logger.info("EditProfileUserController setEditProfileUserrrrrrrr");
        logger.info("Avatar url: " + avatarUrl);
        if (fullName != null) {
            editProfileUser.getUsernameField().setText(fullName);
        }
        if (avatarUrl != null) {
            editProfileUser.setAvatarImage(avatarUrl);
            editProfileUser.getAvatarLabel().setIcon(new ImageIcon(avatarUrl));
            logger.info("EditProfileUserController setAvatarrrrrrrrrrrrrrrr");
        }

        initializeListeners();
    }

    private void initializeListeners() {
        //editProfileUser.addCacelActionListener(e -> editProfileUser.dispose());

        // 👉 Lắng nghe sự kiện chọn ảnh
        editProfileUser.getUploadButton().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh đại diện");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(selectedImageFile);
                    Image scaledImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    editProfileUser.getAvatarLabel().setIcon(new ImageIcon(scaledImage));
                } catch (IOException exception) {
                    exception.printStackTrace(); // Hoặc show thông báo lỗi cho người dùng
                    JOptionPane.showMessageDialog(null, "Không thể đọc ảnh. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        // 👉 Xử lý submit
        editProfileUser.getSubmitButton().addActionListener(e -> {
            String newFullName = editProfileUser.getUsernameField().getText();
            if (newFullName == null || newFullName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tên người dùng không được để trống");
                return;
            }

            UpdateUserRequest request = new UpdateUserRequest();
            request.setFullName(newFullName);

            String jwtToken = TokenManager.getAccessToken();

            UserOther updatedUser = editProfileImpl.editProfile(request, selectedImageFile, jwtToken);

            if (updatedUser != null) {
                JOptionPane.showMessageDialog(null, "Cập nhật thành công");
                eventBus.post(new UserProfileUpdatedEvent(updatedUser.getFullName(), updatedUser.getAvatarUrl()));
                TokenManager.setAvatarUrl(updatedUser.getAvatarUrl());
                TokenManager.setFullName(newFullName);
                editProfileUser.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Cập nhật thất bại");
            }
        });
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
