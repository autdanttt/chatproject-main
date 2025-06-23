package view.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenRequest {
    private String username;


    @JsonProperty("refresh_token")
    private String refreshToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
