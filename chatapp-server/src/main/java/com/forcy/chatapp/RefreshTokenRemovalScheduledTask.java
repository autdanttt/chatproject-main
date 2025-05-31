package com.forcy.chatapp;


import com.forcy.chatapp.token.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RefreshTokenRemovalScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenRemovalScheduledTask.class);

    @Autowired
    RefreshTokenRepository repo;


    @Scheduled(fixedDelayString = "${app.refresh-token.removal.interval}",initialDelay = 5000)
    @Transactional
    public void deleteExpiredRefreshTokens(){
        int tokensDeleted = repo.deleteByExpiryTime();

        LOGGER.info("Number of expired refresh tokens deleted: "  + tokensDeleted);
    }
}
