package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.MessageDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery,Long> {

    @Modifying
    @Query("""
        UPDATE MessageDelivery md
        SET md.seenAt= :seenAt
        WHERE md.user.id = :userId
            AND md.message.group.id = :groupId
            AND md.message.id <= :lastSeenMessageId
            AND md.seenAt IS NULL
""")
    void updateSeenAtUpTo(Long groupId,Long userId, Long lastSeenMessageId, Date seenAt);

    @Query("""
    SELECT md.message FROM MessageDelivery md
    WHERE md.user.id = :userId
      AND md.deliveredAt IS NULL
""")
    List<Message> findUndeliveredMessagesForUser(Long userId);

    @Query("""
        SELECT md
        FROM MessageDelivery md
        WHERE md.message.id = :messageId
""")
    MessageDelivery findByMessageId(Long messageId);



    @Query("""
        SELECT md
        FROM MessageDelivery md
        WHERE md.message.id = :messageId
        AND md.user.id = :userId
""")
    MessageDelivery findByMessageIdAndUserId(Long messageId, Long userId);
}
