package com.ice.exception;

public class InvalidIdException extends RuntimeException {
    private final String code;

    public InvalidIdException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String code() {
        return code;
    }
}