package view.main.rightPanel.otherInfoTop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import payload.CallRequestPayload;
import utility.Config;
import view.ErrorDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CallVideoServiceImpl implements CallVideoService{
    private static final String CALL_REQUEST_URL = Config.BASE_HTTP_URL + "video-call/call-request";

    private Logger logger = LoggerFactory.getLogger(CallVideoServiceImpl.class);

    @Override
    public ResponseEntity<?> sendCallRequest(Long fromUserId, Long toUserId,String fullName, String jwtToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            CallRequestPayload payload = new CallRequestPayload();
            payload.setToUserId(toUserId);
            payload.setFromUserId(fromUserId);
            payload.setCallerName(fullName);

            logger.info("Send payload to user id: " + payload.getToUserId());
            HttpEntity<CallRequestPayload> request = new HttpEntity<>(payload, headers);

            return restTemplate.postForEntity(CALL_REQUEST_URL, request, Map.class);
        } catch (HttpClientErrorException e) {
            ErrorDTO error = new ErrorDTO(new Date(), e.getStatusCode().value(), CALL_REQUEST_URL, new ArrayList<>());
            error.addError("Failed to send call request: " + e.getMessage());
            return new ResponseEntity<>(error, e.getStatusCode());
        } catch (Exception e) {
            ErrorDTO error = new ErrorDTO(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(), CALL_REQUEST_URL, new ArrayList<>());
            error.addError("Failed to send call request: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
