package com.forcy.chatapp.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
