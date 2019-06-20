package com.microweb.exception;

public abstract class AbstractException extends Exception {
    public abstract String getException();

    public abstract String getError();
}