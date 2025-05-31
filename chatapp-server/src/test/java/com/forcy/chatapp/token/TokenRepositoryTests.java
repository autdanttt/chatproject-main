package com.forcy.chatapp.token;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TokenRepositoryTests {
    @Autowired  private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void testRefreshToken() {
        Date now = new Date();
        System.out.println(now);
    }
}
