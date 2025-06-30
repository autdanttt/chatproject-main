package com.forcy.chatapp.entity;

public enum MemberRole {

    ADMIN,      // Quản trị viên, quản lý thành viên
    MODERATOR,  // Quản lý cơ bản, có thể mute, cảnh cáo
    MEMBER,     // Thành viên bình thường
    GUEST       // Thành viên hạn chế (chỉ đọc hoặc giới hạn quyền)
}
