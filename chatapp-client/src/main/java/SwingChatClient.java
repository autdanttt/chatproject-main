
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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class SwingChatClient extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(SwingChatClient.class);

    // Login UI components
    private JTextField loginUsernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JFrame loginFrame;

    // Main UI components
    private JButton connectButton;
    private JTextField usernameField;
    private JTextField messageField;
    private JButton sendButton;
    private JTable messageTable;
    private DefaultTableModel tableModel;

    private ChatStompClient stompClient;
    private String jwtToken;
    private String loggedInUsername;

    public SwingChatClient() {
        logger.info("Starting SwingChatClient application");
        // Initialize login frame
        initLoginFrame();

        // Initialize main frame (hidden initially)
        initMainFrame();
    }

    private void initLoginFrame() {
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        loginFrame.add(new JLabel("Username:"));
        loginUsernameField = new JTextField();
        loginFrame.add(loginUsernameField);

        loginFrame.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginFrame.add(passwordField);

        loginButton = new JButton("Login");
        loginFrame.add(new JLabel()); // Empty cell
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> handleLogin());

        loginFrame.setVisible(true);
        logger.info("Login frame initialized");
    }

    private void initMainFrame() {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout(10, 10));

        // Connect button
        JPanel connectPanel = new JPanel();
        connectButton = new JButton("CONNECT");
        connectButton.addActionListener(e -> connectToWebSocket());
        connectPanel.add(connectButton);
        add(connectPanel, BorderLayout.NORTH);

        // Send message form
        JPanel sendPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        sendPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        sendPanel.add(usernameField);

        sendPanel.add(new JLabel("Message:"));
        messageField = new JTextField();
        sendPanel.add(messageField);

        sendButton = new JButton("SEND");
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> sendMessage());
        sendPanel.add(new JLabel()); // Empty cell
        sendPanel.add(sendButton);

        add(sendPanel, BorderLayout.SOUTH);

        // Messages table
        String[] columnNames = {"Message", "From"};
        tableModel = new DefaultTableModel(columnNames, 0);
        messageTable = new JTable(tableModel);
        messageTable.setEnabled(false);
        add(new JScrollPane(messageTable), BorderLayout.CENTER);

        // Add initial message (like in HTML)
        tableModel.addRow(new Object[]{"Hi!", "Inu"});
        logger.info("Main frame initialized with initial message: Hi! from Inu");
    }

    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            logger.warn("Login attempt with empty username or password");
            JOptionPane.showMessageDialog(loginFrame, "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            logger.info("Attempting login for username: {}", username);
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:8080/api/oauth/login");
            httpPost.setHeader("Content-Type", "application/json");

            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            httpPost.setEntity(new StringEntity(json));

            CloseableHttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());

            // Trích xuất JWT token từ header
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
                logger.info("Login successful for username: {}", loggedInUsername);

                loginFrame.dispose();
                setVisible(true);
            } else {
                logger.warn("Login failed for username: {}. Status code: {}", username, response.getStatusLine().getStatusCode());
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }

            client.close();
        } catch (IOException e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(loginFrame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToWebSocket() {
        if (jwtToken == null) {
            logger.warn("Attempted to connect to WebSocket without JWT token");
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        logger.info("Starting WebSocket connection with URL: ws://localhost:8080/chat, username: {}", loggedInUsername);

        stompClient = new ChatStompClient(
                "ws://localhost:8080/chat",
                loggedInUsername,
                jwtToken,
                message -> {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            logger.info("Received message: {}", message);
                            if (message.startsWith("[")) {
                                tableModel.addRow(new Object[]{message, "Public"});
                            } else {
                                // Kiểm tra xem message có phải JSON không
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    JsonNode data = mapper.readTree(message);
                                    String msgContent = data.get("message").asText();
                                    String from = data.get("from").asText();
                                    tableModel.addRow(new Object[]{msgContent, from});
                                } catch (IOException e) {
                                    // Nếu không phải JSON, hiển thị nguyên văn message
                                    tableModel.addRow(new Object[]{message, "System"});
                                    logger.warn("Failed to parse message as JSON: {}. Error: {}", message, e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            tableModel.addRow(new Object[]{"Error parsing message: " + e.getMessage(), "System"});
                            logger.error("Error parsing message: {}", e.getMessage(), e);
                        }
                    });
                }
        );
        try {
            stompClient.connect();
            logger.info("WebSocket connection established successfully");
            connectButton.setText("Connected");
            connectButton.setEnabled(false);
            sendButton.setEnabled(true);
        } catch (Exception e) {
            logger.error("Error connecting to WebSocket: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Failed to connect: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String toUsername = usernameField.getText();
        String message = messageField.getText();

        if (toUsername.isEmpty() || message.isEmpty()) {
            logger.warn("Send message attempt with empty username or message");
            JOptionPane.showMessageDialog(this, "Please enter username and message", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (stompClient != null) {
//            MessageResponse chatMessage = new MessageResponse(loggedInUsername, toUsername, message);
//            chatMessage.setSender(loggedInUsername);
//            chatMessage.setType("CHAT");
//            stompClient.sendMessage(chatMessage);
//            logger.info("Sent message from {} to {}: {}", loggedInUsername, toUsername, message);
//            messageField.setText("");
        } else {
            logger.warn("Cannot send message: stompClient is null");
        }
    }
    @Override
    public void dispose() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        logger.info("Application closed");
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingChatClient::new);
    }
}