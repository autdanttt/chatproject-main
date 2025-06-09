package com.forcy.chatapp.message;


import com.forcy.chatapp.chat.MessageRepository;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
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
public class MessageRepositoryTests {


    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindMessageUndeliveredByToUserId(){

        User autdant2 = entityManager.find(User.class, 4L);

        List<Message> list = messageRepository.findUndeliveredMessagesForUser(autdant2.getId());

        for(Message m : list){
            System.out.println("Message: " + m.getContent());
        }

        assertThat(list.size()).isGreaterThan(0);

    }


    @Test
    public void testFindLastMessageUndeliveredByUserId(){

        Message list = messageRepository.findTopByChatIdOrderBySentAtDesc(2L);
        System.out.println("Message: " + list.getContent() + list.getSentAt());

        assertThat(list).isNotNull();

     }

}
