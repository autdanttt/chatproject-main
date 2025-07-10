package com.forcy.chatapp.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisKeyExpirationListener redisKeyExpirationListener(RedisTemplate<String, String> redisTemplate,
                                                                 SimpMessagingTemplate simpMessagingTemplate) {
        return new RedisKeyExpirationListener(redisTemplate, simpMessagingTemplate);
    }
//@Bean
//public RedisKeyExpirationListener redisKeyExpirationListener(SimpMessagingTemplate simpMessagingTemplate) {
//    return new RedisKeyExpirationListener(simpMessagingTemplate);
//}

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisKeyExpirationListener listener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listener, new PatternTopic("__keyevent@0__:expired"));

        System.out.println("🟢 RedisMessageListenerContainer is up and listening for expired keys...");
        return container;
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // ✅ Dùng StringRedisSerializer thay vì Jackson
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

}