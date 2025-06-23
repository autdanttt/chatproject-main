package view.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoRefreshScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static final String REFRESH_URL = "http://localhost:10000/api/oauth/token/refresh";

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
            refreshRequest.setUsername(TokenManager.getUsername());
            refreshRequest.setRefreshToken(TokenManager.getRefreshToken());

            HttpEntity<?> entity = new HttpEntity<>(refreshRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    REFRESH_URL,entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.getBody());
                String newAccessToken = node.get("access_token").asText();
                String newRefreshToken = node.get("refresh_token").asText();
                TokenManager.setAccessToken(newAccessToken);
                TokenManager.setRefreshToken(newRefreshToken);
                System.out.println("✅ Refresh token thành công");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi refresh token: " + e.getMessage());
        }
    }
}
