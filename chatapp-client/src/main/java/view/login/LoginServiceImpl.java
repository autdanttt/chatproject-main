package view.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginServiceImpl implements LoginService {


    @Override
    public UserLogin authenticate(String username, String password) throws IOException {
         RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:10000/api/oauth/login";

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
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
            userLogin.setUsername(user.getUsername());
            userLogin.setPhoneNumber(user.getPhoneNumber());
            userLogin.setRoles(user.getRoles());

            TokenManager.setAccessToken(authResponse.getAccessToken());
            TokenManager.setRefreshToken(authResponse.getRefreshToken());
            TokenManager.setUsername(user.getUsername());

            return userLogin;

        } catch (HttpClientErrorException e) {
            UserLogin userLogin = new UserLogin();
            userLogin.setStatusCode(e.getRawStatusCode());
            return userLogin;
        }
    }
}
