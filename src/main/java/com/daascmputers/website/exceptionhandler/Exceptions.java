package com.daascmputers.website.exceptionhandler;

public class Exceptions extends RuntimeException {
    private final String message;

    public Exceptions(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
