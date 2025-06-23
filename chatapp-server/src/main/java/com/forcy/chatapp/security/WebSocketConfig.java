package com.forcy.chatapp.security;

import com.forcy.chatapp.security.jwt.JwtTokenProvider;
import com.forcy.chatapp.security.jwt.JwtValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(){

                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//                        logger.info("🔥 WebSocket Handshake started: {}", request.getURI());
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                });
    }




    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                logger.info("STOMP Command: {}", accessor.getCommand());
//                logger.info("Header: {}", accessor.toNativeHeaderMap());
                assert accessor != null;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwtToken = null;

                    // Kiểm tra header Authorization từ STOMP
                    Map<String, List<String>> nativeHeaders = accessor.toNativeHeaderMap();
                    if (nativeHeaders.containsKey("Authorization")) {
                        String authHeader = nativeHeaders.get("Authorization").get(0);
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            jwtToken = authHeader.substring(7); // Lấy token
                        }
                    }

                    if (jwtToken == null || jwtToken.isEmpty()) {
//                        logger.error("No token found in Authorization header");
                        throw new SecurityException("No token provided");
                    }

                    Authentication authentication = null;
                    try {
                        authentication = jwtTokenProvider.getAuthentication(jwtToken);
                        if (authentication == null) {
//                            logger.error("Authentication returned null for token: {}", jwtToken);
                            throw new SecurityException("Invalid token");
                        }
                    } catch (JwtValidationException e) {
//                        logger.error("JWT validation failed: {}", e.getMessage());
                        throw new SecurityException("Invalid JWT token: " + e.getMessage());
                    }

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                    logger.info("->>>>>>>>>>>>>>>>>>>>>>Xác thực thành công: {}", authentication.getName());
                }
                return message;
            }
        });
    }
}
