package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.Config;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SendOtpApi {
    private static final Logger logger = LoggerFactory.getLogger(SendOtpApi.class);

    public String sendOtp(String email) {
        try {
            URL url = new URL(Config.BASE_HTTP_URL + "api/oauth/forgot-password");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("email", email));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                JsonNode jsonResponse = mapper.readTree(conn.getInputStream());
                String message = jsonResponse.has("message") ? jsonResponse.get("message").asText() : "Đã gửi OTP (nếu email tồn tại)";
                logger.info("OTP API response: {}", message);
                return message;
            } else {
                logger.error("OTP API response code: {}", responseCode);
            }

        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>> Lỗi khi gửi OTP: ", e);
        }
        return null;
    }

    public String resetPasswordByOtp(String email, String otp, String newPassword) {
        try {
            URL url = new URL(Config.BASE_HTTP_URL + "api/oauth/reset-password");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of(
                    "email", email,
                    "token", otp,
                    "new_password", newPassword
            ));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                JsonNode jsonResponse = mapper.readTree(conn.getInputStream());
                String message = jsonResponse.has("message") ? jsonResponse.get("message").asText() : "Đặt lại mật khẩu thành công";
                logger.info("ResetPassword API response: {}", message);
                return message;
            } else {
                logger.error("ResetPassword API response code: {}", responseCode);
            }

        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>> Lỗi khi reset mật khẩu bằng OTP: ", e);
        }
        return null;
    }
}
