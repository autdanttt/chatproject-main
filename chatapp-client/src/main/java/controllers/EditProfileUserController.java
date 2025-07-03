package controllers;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import di.BaseController;
import view.main.dialog.EditProfileUser;
import view.main.rightPanel.components.HeaderRightPanel;

public class EditProfileUserController extends BaseController {
    private EditProfileUser editProfileUser;

    @Inject
    public EditProfileUserController() {

    }

    public void setEditProfileUser(EditProfileUser editProfileUser) {
        this.editProfileUser = editProfileUser;
        initializeListeners();
    }

    private void initializeListeners() {
        editProfileUser.addCacelActionListener(e -> editProfileUser.dispose());

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
