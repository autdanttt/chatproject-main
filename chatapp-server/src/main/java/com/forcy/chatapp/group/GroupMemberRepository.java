package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.ChatGroup;
import com.forcy.chatapp.entity.GroupMember;
import com.forcy.chatapp.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);


    List<GroupMember> findByUserIdAndStatus(Long userId, MemberStatus status);
}
