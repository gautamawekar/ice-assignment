package com.ice.exception;

public class ArtistOfTheDayException extends RuntimeException {
    private final String code;

    public ArtistOfTheDayException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
