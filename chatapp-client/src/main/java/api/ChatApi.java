package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChatGroupResponse;
import model.ChatRequest;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import view.login.TokenManager;

import java.io.File;
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

//    public ChatGroupResponse createGroupChat(String nameGroup, Long currentUserId, List<Long> memberIds) {
//        try {
//            URL url = new URL("http://localhost:10000/api/groups");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Authorization", "Bearer " + TokenManager.getAccessToken());
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("name", nameGroup);
//            requestBody.put("creator_id", currentUserId);
//            requestBody.put("member_ids", memberIds);
//
//            String jsonBody = mapper.writeValueAsString(requestBody);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
//            }
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == 201 || responseCode == 200) {
//                return mapper.readValue(conn.getInputStream(), ChatGroupResponse.class);
//            } else {
//                logger.error("Server response when creating group chat: {}", responseCode);
//                try (InputStream errorStream = conn.getErrorStream()) {
//                    if (errorStream != null) {
//                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
//                        logger.error("Error response body: {}", errorResponse);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error(">>>>>>>>>>>>>>>>>>>> Error when creating group chat: ", e);
//        }
//        return null;
//    }

    public ChatGroupResponse createGroupChat(String nameGroup, Long currentUserId, List<Long> memberIds, File imageFile) {
        try {
            String url = "http://localhost:10000/api/groups";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // 1️⃣ Chuẩn bị JSON cho phần 'group'
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("name", nameGroup);
            groupMap.put("creator_id", currentUserId);
            groupMap.put("member_ids", memberIds);
            String jsonGroup = objectMapper.writeValueAsString(groupMap);

            // 2️⃣ Tạo MultiValueMap cho multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 2.1 Thêm 'group' dưới dạng HttpEntity text/plain
            HttpHeaders jsonHeader = new HttpHeaders();
            jsonHeader.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> jsonPart = new HttpEntity<>(jsonGroup, jsonHeader);
            body.add("group", jsonPart);

            // 2.2 Thêm 'image' nếu có
            if (imageFile != null) {
                FileSystemResource fileResource = new FileSystemResource(imageFile);
                body.add("image", fileResource);
            }

            // 3️⃣ Tạo HttpHeaders chính
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(TokenManager.getAccessToken());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4️⃣ Gửi POST
            ResponseEntity<ChatGroupResponse> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    ChatGroupResponse.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("Server responded with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error when creating group chat", e);
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
