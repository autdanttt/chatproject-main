package view.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

public class RegisterServiceImpl implements RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Override
    public boolean register(String username, String password) {
        return false;
    }

    public RegisterRespone registerApi(String email, String fullName, String password, String confirmPassword) {
        try {
            String url = "http://localhost:10000/api/oauth/register";

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // 1️⃣ Build user JSON
            UserRegister userRegister = new UserRegister(email, fullName, password);
            String jsonUser = objectMapper.writeValueAsString(userRegister);

            // 2️⃣ Build multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            HttpHeaders jsonHeader = new HttpHeaders();
            jsonHeader.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> jsonPart = new HttpEntity<>(jsonUser, jsonHeader);
            body.add("user", jsonPart);

            // 3️⃣ Optionally attach image file if needed
            File imageFile = new File("default.jpg");
            if (imageFile.exists()) {
                FileSystemResource fileResource = new FileSystemResource(imageFile);
                body.add("image", fileResource);
            }

            // 4️⃣ Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 5️⃣ Create HttpEntity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 6️⃣ Execute POST
            ResponseEntity<RegisterRespone> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    RegisterRespone.class
            );

            // 7️⃣ Handle response
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                RegisterRespone resp = response.getBody();
                if (resp != null) {
                    // Hiển thị message
                    javax.swing.JOptionPane.showMessageDialog(null,
                            resp.getMessage(),
                            "Đăng ký thành công",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);

                    java.awt.Desktop.getDesktop().browse(new java.net.URI("https://mail.google.com/"));
                }
                return resp;
            } else {
                logger.error("Server responded with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error when registering user", e);
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Lỗi đăng ký: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }
}
