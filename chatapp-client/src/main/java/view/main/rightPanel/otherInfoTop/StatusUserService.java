package view.main.rightPanel.otherInfoTop;

import utility.StatusNotification;

public interface StatusUserService {
    public StatusNotification fetchStatus(Long otherUserId, String jwtToken);
}
