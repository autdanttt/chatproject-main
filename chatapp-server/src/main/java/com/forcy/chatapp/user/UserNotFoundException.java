package com.forcy.chatapp.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(Long userId) {
        super("User not found with user id: " + userId);
    }
}
