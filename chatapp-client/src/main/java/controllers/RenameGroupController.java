package controllers;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import di.BaseController;
import event.ChatSelectedEvent;
import event.FullNameUpdateEvent;
import event.GroupRenamedEvent;
import model.ChatGroupDTO;
import model.ChatGroupResponse;
import view.main.UserToken;
import view.main.dialog.Rename.RenameGroupDialog;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import view.main.dialog.Rename.RenameGroupImpl;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeature;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeatureController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;


public class RenameGroupController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RenameGroupController.class);
    private RenameGroupDialog renameGroupDialog;
    private RenameGroupImpl renameGroupImpl;
    private String jwtToken;
    private Long IDselected;
    private Consumer<File> imageSelectedListener;
    private String imageUrl;
    private String currentImage;
    private String type;
    private Long UserID;
    private File selectedFile;
    private String currentName;



    @Inject
    public RenameGroupController(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
        this.renameGroupImpl = new RenameGroupImpl();
    }
    @Subscribe
    public void onJwtToken(UserToken userToken) {
        this.jwtToken = userToken.getJwtToken();
        this.UserID = userToken.getUserId();
    }
    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        this.IDselected = event.getChatId();
        this.type = event.getType();
    }
    public void setRenameGroup(RenameGroupDialog renameGroupDialog) {
        this.renameGroupDialog = renameGroupDialog;
            initializeListeners();
    }
    @Subscribe
    public void onFullNameUpdate(FullNameUpdateEvent event) {
        this.currentImage = event.getImageUrl();
        this.currentName = event.getFullName();
    }

    private void initializeListeners() {
        renameGroupDialog.addConfirmListener(event -> {
            if(selectedFile != null) {
                imageUrl = renameGroupImpl.updateGroupImage(IDselected, selectedFile, jwtToken);
            }

            String inputName = renameGroupDialog.getNewGroupName();
            String finalName = currentName;
            boolean nameChanged = inputName != null && !inputName.isEmpty() && !inputName.equals(currentName);
            boolean imageChanged = imageUrl != null && !imageUrl.isEmpty();

            if (nameChanged) {
                ChatGroupDTO updatedGroup = renameGroupImpl.renameGroup(IDselected, inputName, jwtToken);
                if (updatedGroup != null) {
                    finalName = updatedGroup.getGroupName();
                } else {
                    return;
                }
            }

            if (nameChanged || imageChanged) {
                eventBus.post(new FullNameUpdateEvent(finalName, imageUrl));
            }
            renameGroupDialog.dispose();
        });

        renameGroupDialog.addPhotoListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh nhóm");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    try {
                        BufferedImage originalImage = ImageIO.read(selectedFile);
                        Image scaledImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        renameGroupDialog.getAvatarLabel().setIcon(new ImageIcon(scaledImage));
                        renameGroupDialog.revalidate();
                        renameGroupDialog.repaint();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        renameGroupDialog.addCancelListener(e -> renameGroupDialog.dispose());
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
