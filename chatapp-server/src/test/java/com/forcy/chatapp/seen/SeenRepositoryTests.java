package com.forcy.chatapp.seen;


import com.forcy.chatapp.entity.MessageDelivery;
import com.forcy.chatapp.message.MessageDeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SeenRepositoryTests {


    @Autowired
    private MessageDeliveryRepository messageDeliveryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindUpdateSeenMessage() {
        List<MessageDelivery> messageDeliveries = messageDeliveryRepository.findPrivateMessagesToUpdateSeenAt(2L,1L, 11L);
        for (MessageDelivery messageDelivery : messageDeliveries) {
            System.out.println("Message Delivery Id: " + messageDelivery.getId());
        }

        assertThat(messageDeliveries).hasSize(1);
    }

    @Test
    public void testFindUpdateSeenGroupMessage() {
        List<MessageDelivery> messageDeliveries = messageDeliveryRepository.findGroupMessagesToUpdateSeenAt(3L,5L, 31L);
        for (MessageDelivery messageDelivery : messageDeliveries) {
            System.out.println("Message Delivery Id: " + messageDelivery.getId());
        }
        assertThat(messageDeliveries.size()).isGreaterThan(0);
    }


}
