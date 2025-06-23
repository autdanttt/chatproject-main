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

import java.io.IOException;

public class LoginServiceImpl implements LoginService {


    @Override
    public UserLogin authenticate(String username, String password) throws IOException {

        String url = "http://localhost:10000/api/oauth/login";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");

        String json =  String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        httpPost.setEntity(new StringEntity(json));
        CloseableHttpResponse response = client.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity());

        UserLogin userLogin = new UserLogin();


        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if ("Jwt-Token".equalsIgnoreCase(header.getName())) {
                userLogin.setJwtToken(header.getValue());
                break;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);

        if (response.getStatusLine().getStatusCode() == 200) {
            userLogin.setStatusCode(200);
            userLogin.setUsername(jsonNode.get("username").asText());
            userLogin.setUserId(jsonNode.get("id").asLong());
        }else {
            userLogin.setStatusCode(401);
        }

        return userLogin;
    }
}
