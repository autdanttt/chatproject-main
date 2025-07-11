package controllers;

import api.ChatApi;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import custom.ConfirmCustom;
import di.BaseController;
import event.ChatDeletedEvent;
import event.ChatSelectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.main.dialog.CreateChat;
import view.main.dialog.CreateGroupChat;
import view.main.leftPanel.components.FooterPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;

public class FooterLeftController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(FooterLeftController.class);
    private final FooterPanel footerPanel;

    private final CreateChatController createChatController;
    private final CreateChatGroupController createChatGroupController;

    private CreateChat chatDialog;
    private CreateGroupChat chatGroupDialog;
    private ChatApi chatApi;
    private Long chatIdBoth;
    private String type;
    private String basePath = new File(System.getProperty("user.dir")).getParent();


    @Inject
    public FooterLeftController(FooterPanel footerPanel, CreateChatController createChatController, CreateChatGroupController createChatGroupController, ChatApi chatApi, EventBus eventBus) {
        this.footerPanel = footerPanel;
        this.createChatController = createChatController;
        this.createChatGroupController = createChatGroupController;
        this.chatApi = chatApi;
        eventBus.register(this);

        initializeListeners();
    }

    private void initializeListeners() {
        footerPanel.addChatListener(e -> {
            chatDialog = new CreateChat((JFrame) SwingUtilities.getWindowAncestor(footerPanel));
            createChatController.setCreateChat(chatDialog);
            createChatController.showDialogAndLoadUserList();
            chatDialog.dispose();
        });

        footerPanel.addGroupListener(e -> {
            chatGroupDialog = new CreateGroupChat((JFrame) SwingUtilities.getWindowAncestor(footerPanel));
            createChatGroupController.setCreateGroupChat(chatGroupDialog);
            createChatGroupController.showDialogAndLoadUserList();
            chatGroupDialog.dispose();
        });

        footerPanel.deleteChatListener(e -> {
            JFrame mainJFrame = (JFrame) SwingUtilities.getWindowAncestor(footerPanel);
            ImageIcon imageIcon = new ImageIcon(basePath + "/images/QUESTION.png");
            switch (type) {
                case "CHAT":
                    boolean comfirmDelete = ConfirmCustom.showConfirmDialog(mainJFrame,
                            "Bạn có muốn xóa chat này không?",
                            "Xác nhận xóa!",
                            imageIcon,
                            "Hủy",
                            "Xóa");

                    if (comfirmDelete) {
                        boolean successDeleteChat = chatApi.deleteChat(chatIdBoth);
                        eventBus.post(new ChatDeletedEvent(chatIdBoth));
                    }
                    break;
                case "GROUP":
//                    if (!currentUserId.equals(creatorId)) {
//                        JOptionPane.showMessageDialog(mainJFrame,
//                                "Bạn không phải người tạo nhóm, không thể xóa.",
//                                "Không đủ quyền",
//                                JOptionPane.WARNING_MESSAGE);
//                        return; // Không gọi API
//                    }
                    boolean confirmDeleteGroup = ConfirmCustom.showConfirmDialog(mainJFrame,
                            "Bạn có muốn xóa nhóm này không?",
                            "Xác nhận xóa nhóm",
                            imageIcon,
                            "Hủy",
                            "Xóa nhóm");
                    if (confirmDeleteGroup) {
                        boolean successDeleteChatGroup = chatApi.deleteGroupChat(chatIdBoth);
                        eventBus.post(new ChatDeletedEvent(chatIdBoth));
                    }
                    break;
            }
        });
    }

    @Subscribe
    public void addChatIdUserAndGroup(ChatSelectedEvent event) {
        chatIdBoth = event.getChatId();
        type = event.getType();
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
