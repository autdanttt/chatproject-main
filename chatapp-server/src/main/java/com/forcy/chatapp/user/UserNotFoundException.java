package com.forcy.chatapp.chat;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
