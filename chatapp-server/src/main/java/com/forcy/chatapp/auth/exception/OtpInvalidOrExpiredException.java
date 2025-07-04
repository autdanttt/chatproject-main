package com.forcy.chatapp.auth.exception;

public class OtpInvalidOrExpiredException extends RuntimeException {
    public OtpInvalidOrExpiredException(String message) {
        super(message);
    }
}