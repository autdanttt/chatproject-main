package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.GroupMember;
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
public class GroupMemberRepositoryTests {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testFindByGroupId() {
        List<GroupMember> members = groupMemberRepository.findByGroupId(1L);

        assertThat(members.size()).isGreaterThan(0);
    }

    @Test
    public void testFindUsersByGroupIdIsEmpty() {
        List<GroupMember> members = groupMemberRepository.findByGroupId(100L);
    }

    @Test
    public void testFindUsersByGroupIdIsNotEmpty() {
        List<GroupMember> members = groupMemberRepository.findByGroupId(1L);

        members.forEach(System.out::println);

        assertThat(members.size()).isGreaterThan(0);

    }
}
