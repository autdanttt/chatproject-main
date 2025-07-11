package view.login;

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

    public static void setAccessToken(String accessToken) {
        TokenManager.accessToken = accessToken;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        TokenManager.refreshToken = refreshToken;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        TokenManager.email = email;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        TokenManager.fullName = fullName;
    }

    public static String getAvatarUrl() {
        return avatarUrl;
    }

    public static void setAvatarUrl(String avatarUrl) {
        TokenManager.avatarUrl = avatarUrl;
    }

    public static void clear() {
        userId = null;
        accessToken = null;
        refreshToken = null;
        email = null;
        fullName = null;
        avatarUrl = null;
    }
}
