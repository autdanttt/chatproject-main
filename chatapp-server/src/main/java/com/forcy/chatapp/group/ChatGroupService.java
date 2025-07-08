package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.*;
import com.forcy.chatapp.group.dto.ChatGroupDTO;
import com.forcy.chatapp.group.dto.CreateGroupRequest;
import com.forcy.chatapp.group.dto.ChatGroupResponse;
import com.forcy.chatapp.media.AssetService;
import com.forcy.chatapp.message.MessageRepository;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatGroupService {
    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private MessageRepository messageRepository;


    private final Logger logger = LoggerFactory.getLogger(ChatGroupService.class);
    @Autowired
    private AssetService assetService;


    public ChatGroupDTO createGroup(CreateGroupRequest request, MultipartFile multipartFile) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(()-> new UserNotFoundException(request.getCreatorId()));
        String image = "";
        if(multipartFile != null){
            image = assetService.uploadToCloudinary(multipartFile, "group");
        }

        ChatGroup group = new ChatGroup();
        group.setName(request.getName());

        if(!"".equals(image)){
            group.setImage(image);
        }else {
            group.setImage("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751297593/avatar/default.jpg");
        }

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
        groupDTO.setGroupName(group.getName());
        groupDTO.setImage(group.getImage());
        groupDTO.setCreatorId(creator.getId());
        groupDTO.setMemberIds(allMemberIds.stream().toList());
        return groupDTO;
    }
    public String updateImage(Long groupId,Long userId, MultipartFile multipartFile) {
        ChatGroup chatGroup = chatGroupRepository.findById(groupId).orElseThrow(
                () -> new GroupNotFoundException("Group not found with id: " + groupId)
        );


        GroupMember member = getGroupMember(groupId, userId);

        if (member.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("Chỉ ADMIN mới được sửa tên nhóm");
        }

        String image = "";
        if(multipartFile != null){
            assetService.deleteAvatarIfNotDefault(chatGroup.getImage());
            image = assetService.uploadToCloudinary(multipartFile, "group");
        }
        chatGroup.setImage(image);

        ChatGroup saved = chatGroupRepository.save(chatGroup);

        return saved.getImage();
    }


    public ChatGroupDTO updateGroupName(Long groupId, Long userId, String newName){
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        GroupMember member = getGroupMember(groupId, userId);

        if (member.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("Chỉ ADMIN mới được sửa tên nhóm");
        }

        group.setName(newName);
        ChatGroup updatedGroup = chatGroupRepository.save(group);

        ChatGroupDTO groupDTO = new ChatGroupDTO();
        groupDTO.setId(updatedGroup.getId());
        groupDTO.setGroupName(updatedGroup.getName());
        groupDTO.setCreatorId(member.getId());
        logger.info("Creater iddddddddddđ : " + member.getId());

        List<Long> memberListId = new ArrayList<>();
        for (GroupMember user : group.getMembers()) {
            memberListId.add(user.getId());
        }
        groupDTO.setMemberIds(memberListId);

        return groupDTO;
    }

     public GroupMember getGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên nhóm"));
    }



    public void deleteGroup(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        GroupMember member = getGroupMember(groupId, userId);

        if(member.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("Chỉ ADMIN mới được xóa nhóm");
        }

        chatGroupRepository.deleteById(group.getId());
    }


    public List<ChatGroupResponse> getGroupsForSideBar(Long userId){
        List<GroupMember> members = groupMemberRepository.findByUserIdAndStatus(userId,MemberStatus.ACTIVE);

        return members.stream().map(member -> {
            ChatGroup group = member.getGroup();
            Message lastMessage = messageRepository.findTopByGroupIdOrderBySentAtDesc(group.getId());

            String lastContent = lastMessage != null ? lastMessage.getContent() : null;
            String lastSender  = lastMessage != null ? lastMessage.getUser().getFullName() : null;
            Date lastSentAt    = lastMessage != null ? lastMessage.getSentAt() : null;


            return new ChatGroupResponse(
                    group.getId(),
                    group.getName(),
                    group.getImage(),
                    lastContent,
                    lastSender,
                    lastSentAt
            );
        }).collect(Collectors.toList());
    }

}
