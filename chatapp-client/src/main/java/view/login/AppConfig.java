package view.login;

public class AppConfig {
    public static final String BASE_URL = "http://localhost:10000/api";
    public static final String LOGIN_URL = BASE_URL + "/oauth/login";
    public static final String REFRESH_URL = BASE_URL + "/oauth/refresh";
    public static final int TIMEOUT = 5000;
    public static final long AUTO_REFRESH_MARGIN_MILLIS = 60 * 1000;
}
