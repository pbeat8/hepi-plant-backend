package com.hepiplant.backend.exception;

public class ImmutableFieldException extends RuntimeException {

    public ImmutableFieldException() {
    }

    public ImmutableFieldException(String message) {
        super(message);
    }

}
