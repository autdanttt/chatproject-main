package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Chat;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ChatRepositoryTests {


    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddChat() {

        User user1 = entityManager.find(User.class, 4L);
        User user2 = entityManager.find(User.class, 5L);
        Chat chat = new Chat();
        chat.setUsers(new ArrayList<>());
        chatRepository.save(chat);

        user1.getChats().add(chat);
        user2.getChats().add(chat);

        //Optional<Chat> existChat = chatRepository.findChatByTwoUserIds(user1.getId(), user2.getId());
            userRepository.saveAll(List.of(user1, user2));
            chat.setUsers(new ArrayList<>(List.of(user1, user2)));
            Chat savedChat = chatRepository.save(chat);


        assertThat(savedChat.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindChat() {
        User user1 = entityManager.find(User.class, 4L);
        User user2 = entityManager.find(User.class, 5L);


        Optional<Chat> chatBetweenUsers= chatRepository.findChatByTwoUserIds(user1.getId(), user2.getId());

        assertThat(chatBetweenUsers.get().getUsers().size()).isEqualTo(2);
        assertThat(chatBetweenUsers.get().getId()).isEqualTo(3);
    }

    @Test
    public void testFindMessageByUserId() {

        User user1 = entityManager.find(User.class, 5L);

        List<Chat> list = chatRepository.findByUserId(user1.getId());

        for (Chat chat : list) {
            System.out.println("Chat: " + chat);
        }
        assertThat(list.size()).isGreaterThan(0);
    }


}
