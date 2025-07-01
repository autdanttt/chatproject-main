package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChatGroupResponse;
import model.ChatRequest;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.TokenManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                logger.error("Server response: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("->>>>>>>>>>>>>>>>>>>> Error when you created chat: ", e);
        }
        return null;
    }

    public boolean deleteChat(Long chatID) {
        try {
            URL url = new URL("http://localhost:10000/chats/" + chatID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            conn.setRequestProperty("Authorization", "Bearer " + TokenManager.getAccessToken());

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
                return true;
            } else {
                logger.error("Failed to delete chat. Server responded with code: {}", responseCode);
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        logger.error("Lỗi body: {}", errorResponse);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Lỗi xóa chat với id {}: {}", chatID, e.getMessage(), e);
        }

        return false;
    }

    public ChatGroupResponse createGroupChat(String nameGroup, Long currentUserId, List<Long> memberIds) {
        try {
            URL url = new URL("http://localhost:10000/api/groups");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + TokenManager.getAccessToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Tạo JSON body
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", nameGroup);
            requestBody.put("creator_id", currentUserId);
            requestBody.put("member_ids", memberIds);

            String jsonBody = mapper.writeValueAsString(requestBody);

            // Gửi request body
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                return mapper.readValue(conn.getInputStream(), ChatGroupResponse.class);
            } else {
                logger.error("Server response when creating group chat: {}", responseCode);
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        logger.error("Error response body: {}", errorResponse);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>>>>>>>> Error when creating group chat: ", e);
        }
        return null;
    }

    public boolean deleteGroupChat(Long groupId) {
        try {
            URL url = new URL("http://localhost:10000/api/groups/" + groupId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            conn.setRequestProperty("Authorization", "Bearer " + TokenManager.getAccessToken());

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
                return true;
            } else {
                logger.error("Failed to delete group chat. Server responded with code: {}", responseCode);
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        logger.error("Lỗi body: {}", errorResponse);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Lỗi xóa group chat với id {}: {}", groupId, e.getMessage(), e);
        }

        return false;
    }

}
