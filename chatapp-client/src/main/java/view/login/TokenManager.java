package view.login;

public class TokenManager {
    private static String accessToken;
    private static String refreshToken;
    private static String email;

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
}
