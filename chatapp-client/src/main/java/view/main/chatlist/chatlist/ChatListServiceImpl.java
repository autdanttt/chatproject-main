package view.main.chatlist.chatlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import view.login.TokenManager;

import java.net.HttpURLConnection;
import java.net.URL;

public class ChatListServiceImpl implements ChatListService {
    private final Logger logger = LoggerFactory.getLogger(ChatListServiceImpl.class);
    @Override
    public ChatResponse[] getChatList(String jwtToken) {
        try {
            String url = "http://localhost:10000/chats";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ChatResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ChatResponse[].class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ChatResponse[] chatList = response.getBody();
                logger.info("Chat list response: {}", chatList != null ? chatList.length : 0);
                return chatList != null ? chatList : new ChatResponse[0];
            }

        } catch (Exception e) {
            logger.error("Error while fetching chat list", e);
        }

        logger.info("No chat list found");
        return new ChatResponse[0];
    }
}
