package view.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import utility.Config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoRefreshScheduler {
    private final Logger logger = LoggerFactory.getLogger(AutoRefreshScheduler.class);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static final String REFRESH_URL = Config.BASE_HTTP_URL + "api/oauth/token/refresh";

    public static void start() {
        scheduler.scheduleAtFixedRate(() -> {
            String token = TokenManager.getAccessToken();
            if (token != null && JwtUtils.isTokenExpiringSoon(token)) {
                System.out.println("🔁 Token sắp hết, đang làm mới...");
                refreshAccessToken();
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    private static void refreshAccessToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
            refreshRequest.setEmail(TokenManager.getEmail());
            refreshRequest.setRefreshToken(TokenManager.getRefreshToken());

            HttpEntity<?> entity = new HttpEntity<>(refreshRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    REFRESH_URL,entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.getBody());
                String newAccessToken = node.get("access_token").asText();
                String newRefreshToken = node.get("refresh_token").asText();
                System.out.println("Refresh Token: " + newRefreshToken);
                System.out.println("New Access Token: " + newAccessToken);
                TokenManager.setAccessToken(newAccessToken);
                TokenManager.setRefreshToken(newRefreshToken);
                System.out.println("✅ Refresh token thành công");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi refresh token: " + e.getMessage());
        }
    }
}
