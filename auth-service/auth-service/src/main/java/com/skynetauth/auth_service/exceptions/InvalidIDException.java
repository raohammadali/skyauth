package com.skynetauth.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidIDException extends RuntimeException {
    /**
     * Constructs an InvalidIDException with the given detail message.
     *
     * @param type the detail message describing the invalid identifier
     */
    public InvalidIDException(String type) {
        super(type);
    }
}
