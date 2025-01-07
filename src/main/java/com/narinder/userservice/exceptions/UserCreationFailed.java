package com.narinder.userservice.exceptions;

public class UserCreationFailed extends RuntimeException {

    public UserCreationFailed(String message) {
        super(message);
    }

}
