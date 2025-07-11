package view.main.dialog.Rename;

import model.ChatGroupDTO;

import java.io.File;

public interface RenameGroupService {
    public ChatGroupDTO renameGroup(Long GroupId, String newGroupName, String jwtToken);
    public String updateGroupImage(Long groupId, File imageFile, String jwtToken);
}
