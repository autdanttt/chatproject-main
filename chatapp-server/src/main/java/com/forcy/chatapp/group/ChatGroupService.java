package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.*;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChatGroupService {
    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;



    public ChatGroupDTO createGroup(CreateGroupRequest request) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(()-> new UserNotFoundException(request.getCreatorId()));

        ChatGroup group = new ChatGroup();
        group.setName(request.getName());

        chatGroupRepository.save(group);

        Set<Long> allMemberIds = new HashSet<>(request.getMemberIds());
        allMemberIds.add(creator.getId()); // dam bao creator luon co mat

        for (Long memberId : allMemberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(()-> new UserNotFoundException(memberId));

            GroupMember member = new GroupMember();
            member.setUser(user);
            member.setGroup(group);
            member.setJoinedAt(new Date());
            member.setMuted(false);
            member.setBanned(false);
            member.setStatus(MemberStatus.ACTIVE);
            member.setRole(memberId.equals(creator.getId()) ? MemberRole.ADMIN : MemberRole.MEMBER);
            member.setAdmin(memberId.equals(creator.getId()));

            groupMemberRepository.save(member);
        }


        ChatGroupDTO groupDTO = new ChatGroupDTO();
        groupDTO.setId(group.getId());
        groupDTO.setName(group.getName());
        groupDTO.setCreatorId(creator.getId());
        groupDTO.setMemberIds(allMemberIds.stream().toList());

        return groupDTO;
    }



}
