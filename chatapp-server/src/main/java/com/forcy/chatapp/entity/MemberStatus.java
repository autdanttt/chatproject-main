package com.forcy.chatapp.entity;

public enum MemberStatus {
    INVITED,    // đã được mời nhưng chưa tham gia
    ACTIVE,     // đang ở trong nhóm
    MUTED,      // đang bị tắt quyền nói
    LEFT,       // đã tự rời nhóm
    REMOVED,    // bị admin xóa khỏi nhóm
    BANNED,     // bị cấm và không được vào lại
    READ_ONLY   // chỉ được đọc, không được gửi tin nhắn
}
