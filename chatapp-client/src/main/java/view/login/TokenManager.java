package view.login;

import lombok.Getter;
import lombok.Setter;
import view.main.dialog.EditProfileUser.EditProfileUser;
@Getter
@Setter
public class TokenManager {
    private static Long userId;
    private static String accessToken;
    private static String refreshToken;
    private static String email;
    private static String fullName;
    private static String avatarUrl;

    public static Long getUserId() {
        return userId;
    }

    public static void setUserId(Long userId) {
        TokenManager.userId = userId;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String token) {
        refreshToken = token;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        TokenManager.email = email;
    }

    public static void clear() {
        userId = null;
        accessToken = null;
        refreshToken = null;
        email = null;
        fullName = null;
        avatarUrl = null;
    }

    public static void setFullName(String fullName) {
        TokenManager.fullName = fullName;
    }

    public static void setAvatarUrl(String avatarUrl) {
        TokenManager.avatarUrl = avatarUrl;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getAvatarUrl() {
        return avatarUrl;
    }
}
