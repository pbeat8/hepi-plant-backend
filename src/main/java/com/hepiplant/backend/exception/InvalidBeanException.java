package com.hepiplant.backend.exception;

public class InvalidBeanException extends RuntimeException {

    public InvalidBeanException() {
    }

    public InvalidBeanException(String message) {
        super(message);
    }

}
