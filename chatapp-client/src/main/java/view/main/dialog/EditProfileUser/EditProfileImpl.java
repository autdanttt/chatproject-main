package view.main.dialog.EditProfileUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.UpdateUserRequest;
import model.UserOther;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.List;


public class EditProfileImpl implements EditProfileService {
    private static final Logger logger = LoggerFactory.getLogger(EditProfileImpl.class);

    @Override
    public UserOther editProfile(UpdateUserRequest updateUserRequest, File imageFile, String jwtToken) {
        try {
            String url = "http://localhost:10000/api/users/update";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonUser = objectMapper.writeValueAsString(updateUserRequest);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> userPart = new HttpEntity<>(jsonUser, jsonHeaders);
            body.add("user", userPart);

            if (imageFile != null && imageFile.exists()) {
                FileSystemResource imageResource = new FileSystemResource(imageFile);
                body.add("image", imageResource);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(jwtToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<UserOther> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    UserOther.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.error("❌ Cập nhật thất bại: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("❌ Lỗi khi cập nhật hồ sơ", e);
        }

        return null;
    }
}
