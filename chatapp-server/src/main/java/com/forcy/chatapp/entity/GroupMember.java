package com.forcy.chatapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "groups_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ChatGroup group;


    private boolean isAdmin;         // Có phải admin nhóm?
    private boolean isMuted;         // Có bị tắt thông báo nhóm không?
    private boolean isBanned;

    private Date joinedAt;           // Thời điểm tham gia nhóm
    private Date leftAt;             // Thời điểm rời nhóm (nullable)
    private Date lastSeenAt;         // Thời điểm cuối cùng xem tin nhắn trong nhóm
    private Date mutedUntil;         // Nếu bị mute tạm thời

    // Bạn có thể dùng thêm enum để kiểm soát trạng thái
    @Enumerated(EnumType.STRING)
    private MemberStatus status; // ACTIVE, LEFT, BANNED, MUTED

    @Enumerated(EnumType.STRING)
    private MemberRole role;


}
