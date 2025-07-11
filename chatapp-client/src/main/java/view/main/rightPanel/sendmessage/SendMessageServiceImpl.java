package view.main.rightPanel.sendmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.GroupMessageRequest;
import model.MessageRequest;
import model.MessageResponse;
import model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import utility.Config;
import view.ApiResult;
import view.ErrorDTO;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SendMessageServiceImpl implements SendMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageServiceImpl.class);

    @Override
    public ApiResult<MessageResponse> sendTextMessage(String jwtToken, Long fromUserId, Long toUserId, String messageContent, MessageType messageType) {
        try {
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setFromUserId(fromUserId);
            messageRequest.setToUserId(toUserId);
            messageRequest.setMessageType(messageType);
            messageRequest.setContent(messageContent);

            LOGGER.info("Message request: {}", messageRequest);

            String url = Config.BASE_HTTP_URL + "messages";
            LOGGER.info("JWT token: {}", jwtToken);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<MessageRequest> entity = new HttpEntity<>(messageRequest, headers);

            ResponseEntity<MessageResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, MessageResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Message sent: {}", response.getBody());
                LOGGER.info("Response code: {}", response.getStatusCode());
            }else {
                LOGGER.info("RESPONSE CODE: {}", response.getStatusCode());
            }

            return ApiResult.ok(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            try {
                String body = ex.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                ErrorDTO errorDTO = objectMapper.readValue(body, ErrorDTO.class);
                return ApiResult.fail(errorDTO);
            } catch (Exception parseEx) {
                return ApiResult.fail(new ErrorDTO(new Date(), 500, "/messages", List.of(parseEx.getMessage())));
            }
        } catch (Exception e) {
            return ApiResult.fail(new ErrorDTO(new Date(),500,"/messages",List.of(e.getMessage())));
        }
    }

    @Override
    public ResponseEntity<Map> sendFile(String jwtToken, Long userId, File file) {

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        uploadHeaders.set("Authorization", "Bearer " + jwtToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userId", userId);
        body.add("mediaFile", new FileSystemResource(file));


        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, uploadHeaders);
        String urlUpload =Config.BASE_HTTP_URL +  "v1/media/upload";
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForEntity(urlUpload, requestEntity, Map.class);
    }

    @Override
    public ApiResult<MessageResponse> sendTextGroupMessage(String jwtToken, Long userId, Long chatId, String messageContent,MessageType messageType) {
        try {
            GroupMessageRequest groupMessageRequest = new GroupMessageRequest();
            groupMessageRequest.setFromUserId(userId);
            groupMessageRequest.setToGroupId(chatId);
            groupMessageRequest.setMessageType(messageType);
            groupMessageRequest.setContent(messageContent);

            LOGGER.info("Message request: {}", groupMessageRequest);

            String url = Config.BASE_HTTP_URL+"messages" + "/group";
            LOGGER.info("JWT token: {}", jwtToken);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            HttpEntity<GroupMessageRequest> entity = new HttpEntity<>(groupMessageRequest, headers);

            ResponseEntity<MessageResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, MessageResponse.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Message sent: {}", response.getBody());
                LOGGER.info("Response code: {}", response.getStatusCode());
            }else {
                LOGGER.info("RESPONSE CODE: {}", response.getStatusCode());
            }

            return ApiResult.ok(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            try {
                String body = ex.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                ErrorDTO errorDTO = objectMapper.readValue(body, ErrorDTO.class);
                return ApiResult.fail(errorDTO);
            } catch (Exception parseEx) {
                return ApiResult.fail(new ErrorDTO(new Date(), 500, "/messages/group", List.of(parseEx.getMessage())));
            }
        } catch (Exception e) {
            return ApiResult.fail(new ErrorDTO(new Date(),500,"/messages/group",List.of(e.getMessage())));
        }
    }
}
