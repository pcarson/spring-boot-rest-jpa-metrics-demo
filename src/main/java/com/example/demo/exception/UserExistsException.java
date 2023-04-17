package com.example.demo.exception;

public class UserExistsException extends Exception {

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
