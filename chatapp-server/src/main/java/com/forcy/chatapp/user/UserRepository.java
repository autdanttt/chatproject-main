package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email= ?1")
    public User getUserByEmail(String email);




    @Modifying
    @Query("""
        DELETE FROM User u
        WHERE u.isVerified = false AND u.createAt < :expiredDate
""")
    int deleteUnverifiedUsersBefore(@Param("expiredDate") Date expiredDate);
}
