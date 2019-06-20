package com.microweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends AbstractException {
    private final Class entityClassName;
    private final String message;


    public NotFoundException(final Class entity, final String message) {
        this.entityClassName = entity;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return entityClassName.getSimpleName() + ": " + message;
    }

    @Override
    public String getException() {
        return Arrays.toString(getStackTrace());
    }

    @Override
    public String getError() {
        return "Entity Not Found";
    }
}