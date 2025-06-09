package com.forcy.chatapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToMany(mappedBy = "chats")
    private List<User> users;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id + "}";
    }
}
