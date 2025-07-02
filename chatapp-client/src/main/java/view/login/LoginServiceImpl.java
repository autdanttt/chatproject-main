package view.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginServiceImpl implements LoginService {
    private final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Override
    public UserLogin authenticate(String email, String password) throws IOException {
         RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:10000/api/oauth/login";

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);

        // Tạo HTTP entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AuthResponse.class
            );

            AuthResponse authResponse = response.getBody();
            AuthUserDTO user = authResponse.getUser();

            UserLogin userLogin = new UserLogin();
            userLogin.setStatusCode(response.getStatusCodeValue());
            userLogin.setUserId(user.getId());
            userLogin.setFullName(user.getFullName());
            userLogin.setEmail(user.getEmail());
            logger.info("Avatar url: " + user.getAvatarUrl());
            userLogin.setAvatarUrl(user.getAvatarUrl());
            userLogin.setRoles(user.getRoles());

            TokenManager.setAccessToken(authResponse.getAccessToken());
            TokenManager.setRefreshToken(authResponse.getRefreshToken());
            TokenManager.setEmail(user.getEmail());

            return userLogin;

        } catch (HttpClientErrorException e) {
            UserLogin userLogin = new UserLogin();
            userLogin.setStatusCode(e.getRawStatusCode());
            return userLogin;
        }
    }
}
