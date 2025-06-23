package view.main.rightpanel.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class MessageServiceImpl implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    @Override
    public MessageResponse[] getMessageByChatId(Long chatId, String jwtToken) {
        try {
            String url = "http://localhost:10000/chats/" + chatId + "/messages";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<MessageResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    MessageResponse[].class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody() != null ? response.getBody() : new MessageResponse[0];
            }

        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách tin nhắn", e);
        }

        return new MessageResponse[0];
    }
}
