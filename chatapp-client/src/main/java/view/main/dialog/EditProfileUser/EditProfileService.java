package view.main.dialog.EditProfileUser;

import model.UpdateUserRequest;
import model.UserOther;

import java.io.File;

public interface EditProfileService {
    public UserOther editProfile(UpdateUserRequest updateUserRequest, File imageFile, String jwtToken);
}
