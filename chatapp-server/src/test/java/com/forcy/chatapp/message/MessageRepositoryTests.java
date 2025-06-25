package com.forcy.chatapp.message;


import com.forcy.chatapp.entity.ChatGroup;
import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.MessageType;
import com.forcy.chatapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
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
    public void testSendMessageToGroup() {
        User user = entityManager.find(User.class, 2L);

        ChatGroup chatGroup = entityManager.find(ChatGroup.class, 2L);

        Message message = new Message();
        message.setUser(user);
        message.setGroup(chatGroup);
        message.setType(MessageType.TEXT);
        message.setContent("Hello, This is a test message in group 2");
        message.setSentAt(new Date());

        Message savedMessage = messageRepository.save(message);

        assertThat(savedMessage).isNotNull();
    }

    @Test
    public void testFindMessageUndeliveredByToUserId(){

//        User autdant2 = entityManager.find(User.class, 4L);
//
//        List<Message> list = messageRepository.findUndeliveredMessagesForUser(autdant2.getId());
//
//        for(Message m : list){
//            System.out.println("Message: " + m.getContent());
//        }
//
//        assertThat(list.size()).isGreaterThan(0);

    }


    @Test
    public void testFindLastMessageUndeliveredByUserId(){

        Message list = messageRepository.findTopByChatIdOrderBySentAtDesc(2L);
        System.out.println("Message: " + list.getContent() + list.getSentAt());

        assertThat(list).isNotNull();

     }

}
