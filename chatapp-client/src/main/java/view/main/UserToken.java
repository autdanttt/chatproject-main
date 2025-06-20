package view.main;

public class UserToken {
    private String jwtToken;
    private Long userId;
    private String username;

    public UserToken(String jwtToken, Long userId, String username) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.username = username;
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
}
