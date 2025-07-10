package view.main.rightPanel.otherInfoTop;

import model.MessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import utility.StatusNotification;

public class StatusUserServiceImpl implements StatusUserService {
    private static final Logger logger = LoggerFactory.getLogger(StatusUserServiceImpl.class);
    private static final String BASE_URL = "http://localhost:10000/api/status/";

    @Override
    public StatusNotification fetchStatus(Long otherUserId, String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<StatusNotification> response = restTemplate.exchange(
                    BASE_URL + otherUserId,
                    HttpMethod.GET,
                    entity,
                    StatusNotification.class
            );

            logger.info("✅ Fetched status from {}", BASE_URL + otherUserId);
            return response.getBody();

        } catch (Exception e) {
            logger.error("❌ Error fetching status from {}", BASE_URL + otherUserId, e);
            // Trả về trạng thái không rõ
            return new StatusNotification(null, "unknown", null);
        }
    }
}

