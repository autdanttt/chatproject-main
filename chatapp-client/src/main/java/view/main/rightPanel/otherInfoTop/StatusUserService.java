package view.main.rightPanel.otherInfoTop;

public interface StatusUserService {
    public UserStatus fetchStatus(Long otherUserId, String jwtToken);
}
