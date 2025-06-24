package view.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtils {
    public static final long AUTO_REFRESH_MARGIN_MILLIS = 60 * 1000;

    public static long extractExpiration(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);
            return node.get("exp").asLong() * 1000;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isTokenExpiringSoon(String token) {
        long now = System.currentTimeMillis();
        boolean expired = extractExpiration(token) > now;
        return (extractExpiration(token) - now) < AUTO_REFRESH_MARGIN_MILLIS;
    }
}
