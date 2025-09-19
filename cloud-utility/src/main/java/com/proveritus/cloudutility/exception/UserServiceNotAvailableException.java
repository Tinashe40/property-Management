package com.proveritus.cloudutility.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class UserServiceNotAvailableException extends RuntimeException {

    public UserServiceNotAvailableException(String message) {
        super(message);
    }

    public UserServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
