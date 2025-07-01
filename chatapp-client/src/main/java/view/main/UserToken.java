package view.main;

public class UserToken {
    private String jwtToken;
    private Long userId;
    private String username;
    private String avatarUrl;

    public UserToken(String jwtToken, Long userId, String username, String avatarUrl) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
