package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("""
        SELECT c FROM Chat c 
        JOIN c.users u1 
        JOIN c.users u2
        WHERE u1.id = :user1Id AND u2.id = :user2Id
""")
    Optional<Chat> findChatByTwoUserIds(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);


    @Query("""
        SELECT c FROM Chat c
        JOIN c.users u
        WHERE u.id = :userId
""")
    public List<Chat> findByUserId(Long userId);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Chat c JOIN c.users u WHERE c.id = :chatId AND u.id = :userId
""")
    boolean existsByIdAndUserId(Long chatId, Long userId);
}
