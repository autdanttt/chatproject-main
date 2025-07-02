package com.forcy.chatapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;


    @ManyToOne
    @JoinColumn(name = "group_id")
    private ChatGroup group;


    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageDelivery> deliveries;

    @Enumerated(EnumType.STRING)
    private MessageType type;


    @Column(columnDefinition = "TEXT")
    private String content;

    private Date sentAt;
}
