package com.ice.exception;

public class InvalidInputException extends RuntimeException {
    private final String code;

    public InvalidInputException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String code() {
        return code;
    }
}