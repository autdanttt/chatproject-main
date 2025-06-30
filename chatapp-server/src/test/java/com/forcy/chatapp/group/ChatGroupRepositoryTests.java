package com.forcy.chatapp.group;


import com.forcy.chatapp.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ChatGroupRepositoryTests {

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createGroup(){

        User creator = entityManager.find(User.class, 1L);

        String groupName = "Test Group 1";
        ChatGroup group = new ChatGroup();
        group.setName(groupName);

        ChatGroup savedChatGroup = chatGroupRepository.save(group);
        assertThat(group.getName()).isEqualTo(savedChatGroup.getName());
        assertThat(savedChatGroup.getId()).isNotNull();

        Set<Long> allMemberIds = new HashSet<>();
        allMemberIds.add(creator.getId());
        allMemberIds.add(2L);
        allMemberIds.add(3L);

        for (Long memberId : allMemberIds) {
            User user = entityManager.find(User.class, memberId);

            GroupMember member = new GroupMember();
            member.setUser(user);
            member.setGroup(savedChatGroup);
            member.setJoinedAt(new Date());
            member.setMuted(false);
            member.setBanned(false);
            member.setStatus(MemberStatus.ACTIVE);
            member.setRole(memberId.equals(creator.getId()) ? MemberRole.ADMIN : MemberRole.MEMBER);
            member.setAdmin(memberId.equals(creator.getId()));


            GroupMember groupMember = groupMemberRepository.save(member);
            assertThat(groupMember.getId()).isNotNull();
            assertThat(groupMember.getJoinedAt()).isNotNull();

        }


    }


    @Test
    public void testFindByUserIdAndGroupIdNotFound(){
        Long userId = 100L;
        Long groupId = 112L;

        Optional<GroupMember> groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId);

        assertThat(groupMember.isPresent()).isFalse();


    }

    @Test
    public void testFindByUserIdAndGroupIdFound(){
        Long userId = 1L;
        Long groupId = 1L;

        Optional<GroupMember> groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId);
        System.out.println("Group member id " + groupMember.get().getId());

        assertThat(groupMember.isPresent()).isTrue();
        assertThat(groupMember.get().getId()).isGreaterThan(0);


    }


    @Test
    public void testDeleteGroupByIdNotFound(){
        Long groupId = 100L;

        chatGroupRepository.deleteById(groupId);

        assertThat(groupMemberRepository.findById(groupId).isPresent()).isFalse();

    }


    @Test
    public void testDeleteGroupById(){
        Long groupId = 4L;

        chatGroupRepository.deleteById(groupId);

        assertThat(groupMemberRepository.findById(groupId).isPresent()).isFalse();
    }
}
