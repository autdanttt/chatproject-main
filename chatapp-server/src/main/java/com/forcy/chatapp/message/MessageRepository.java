package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

//    @Query("""
//        SELECT m FROM Message m
//        WHERE m.chat IN (
//            SELECT c FROM Chat c
//            WHERE :toUserId IN (
//                SELECT u.id FROM c.users u
//            )
//        ) AND m.deliveredAt IS NULL AND m.user.id != :toUserId
//""")
//    public List<Message> findUndeliveredMessagesForUser(@Param("toUserId") Long toUserId);

    public Message findTopByChatIdOrderBySentAtDesc(Long chatId);

    public Message findTopByGroupIdOrderBySentAtDesc(Long groupId);


    public List<Message> findByChatId(Long chatId);


}
