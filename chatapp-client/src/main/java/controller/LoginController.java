package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.ChatView;
import view.LoginView;

import javax.swing.*;

import java.io.IOException;


public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private LoginView view;
    private ChatView chatView;
    private ChatController chatController;

    public LoginController(LoginView view){
        this.view = view;
        initializeListeners();
    }

    private void initializeListeners() {
        view.getLoginButton().addActionListener(e -> performLogin());
        
    }

    private void performLogin() {
        String username = view.getUsernameField().getText().trim();
        String password = new String(view.getPasswordField().getPassword()).trim();


        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Please enter username and password");
            return;
        }

        try{
            logger.info("Attempting to login user: " + username);
            String url = "http://localhost:10000/api/oauth/login";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");

            String json =  String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            logger.info("username and password: " + username + password);
            httpPost.setEntity(new StringEntity(json));

            CloseableHttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            String jwtToken = null;
            String loggedInUsername = null;
            Long loggedUserId = null;

            // Trich xuat JWT Token tu header
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                if ("Jwt-Token".equalsIgnoreCase(header.getName())) {
                    jwtToken = header.getValue();
                    logger.info("Extracted JWT token: {}", jwtToken);
                    break;
                }
            }


            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);

            if (response.getStatusLine().getStatusCode() == 200) {
                loggedInUsername = jsonNode.get("username").asText();
                loggedUserId = jsonNode.get("id").asLong();

                logger.info("Login successful for username: {}", loggedInUsername);
                view.getFrame().setVisible(true);
                chatView = new ChatView(loggedInUsername, chatController);
                view.getFrame().dispose();

                chatController = new ChatController(chatView,loggedUserId, loggedInUsername,jwtToken);
            } else {
                logger.warn("Login failed for username: {}. Status code: {}", username, response.getStatusLine().getStatusCode());
                JOptionPane.showMessageDialog(view.getFrame(), "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
            client.close();

        }catch (IOException e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(view.getFrame(), "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
