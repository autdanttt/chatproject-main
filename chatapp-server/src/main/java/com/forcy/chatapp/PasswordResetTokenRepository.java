package com.forcy.chatapp;

import com.forcy.chatapp.entity.PasswordResetToken;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    @Query("""
        SELECT t FROM PasswordResetToken t
        WHERE t.token=:token
        AND t.email=:email
""")
    Optional<PasswordResetToken> findByTokenAndEmail(@Param("token") String token, @Param("email") String email);
}
