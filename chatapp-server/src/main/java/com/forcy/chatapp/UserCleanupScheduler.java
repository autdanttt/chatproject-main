package com.forcy.chatapp;

import com.forcy.chatapp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@EnableScheduling
public class UserCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(UserCleanupScheduler.class);

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedDelayString = "${app.unverified-user.removal.interval}", initialDelay = 10000)
    @Transactional
    public void deleteUnverifiedUsers() {
        Date expiredDate = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        int deleted = userRepository.deleteUnverifiedUsersBefore(expiredDate);
        logger.info("Đã xóa " + deleted + " tài khoản chưa xác thực quá hạn.");
    }

}
