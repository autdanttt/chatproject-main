package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.*;
import com.forcy.chatapp.group.dto.ChatGroupDTO;
import com.forcy.chatapp.group.dto.CreateGroupRequest;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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


    public ChatGroupDTO updateGroupName(Long groupId, Long userId, String newName){
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        GroupMember member = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên nhóm"));

        if (member.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("Chỉ ADMIN mới được sửa tên nhóm");
        }

        group.setName(newName);
        ChatGroup updatedGroup = chatGroupRepository.save(group);

        ChatGroupDTO groupDTO = new ChatGroupDTO();
        groupDTO.setId(updatedGroup.getId());
        groupDTO.setName(updatedGroup.getName());
        groupDTO.setCreatorId(member.getId());

        List<Long> memberListId = new ArrayList<>();
        for (GroupMember user : group.getMembers()) {
            memberListId.add(user.getId());
        }
        groupDTO.setMemberIds(memberListId);

        return groupDTO;
    }



}
