package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.ChatGroup;
import com.forcy.chatapp.entity.GroupMember;
import com.forcy.chatapp.entity.MemberStatus;
import com.forcy.chatapp.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);


    List<GroupMember> findByUserIdAndStatus(Long userId, MemberStatus status);

    List<GroupMember> findByGroupId(Long toGroupId);

    @Query("""
        SELECT gm.user
        FROM GroupMember gm
        WHERE gm.group.id = :groupId
""")
    List<User> findUsersByGroupId(@Param("groupId") Long groupId);
}
