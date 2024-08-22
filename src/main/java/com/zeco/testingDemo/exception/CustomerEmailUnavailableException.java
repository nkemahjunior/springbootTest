package com.zeco.testingDemo.exception;

public class CustomerEmailUnavailableException extends RuntimeException {

    public CustomerEmailUnavailableException(String message) {
        super(message);
    }
}