package com.forcy.chatapp.token;

import com.forcy.chatapp.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.email = ?1")
    public List<RefreshToken> findByEmail(String email);

    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryTime <= CURRENT_TIME")
    @Modifying
    public int deleteByExpiryTime();
}
