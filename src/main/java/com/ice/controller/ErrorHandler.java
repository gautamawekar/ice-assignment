package com.ice.controller;

import com.ice.exception.ArtistOfTheDayException;
import com.ice.exception.InvalidIdException;
import com.ice.exception.InvalidInputException;
import com.ice.model.Error;
import com.ice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(exception = {InvalidInputException.class, InvalidIdException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidInputException(InvalidInputException e) {
        return new ErrorResponse(List.of(new Error(e.code(), e.getMessage())));
    }

    @ExceptionHandler(ArtistOfTheDayException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleArtistOfTheDayException(ArtistOfTheDayException e) {
        return new ErrorResponse(List.of(new Error(e.code(), e.getMessage())));
    }
}
