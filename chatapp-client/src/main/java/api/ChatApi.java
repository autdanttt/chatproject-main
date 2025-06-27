package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChatRequest;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.TokenManager;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatApi {
    private static final Logger logger = LoggerFactory.getLogger(ChatApi.class);

    public ChatResponse createChat(Long targetUserId) {
        try {
            URL url = new URL("http://localhost:10000/chats");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + TokenManager.getAccessToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ChatRequest request = new ChatRequest(targetUserId);
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(request);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                return mapper.readValue(conn.getInputStream(), ChatResponse.class);
            } else {
                logger.error("Server response: " + responseCode);
            }
        } catch (Exception e) {
            logger.error("->>>>>>>>>>>>>>>>>>>> Error when you created chat: ", e);
        }
        return null;
    }
}
