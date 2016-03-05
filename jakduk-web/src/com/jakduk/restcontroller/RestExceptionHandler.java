package com.jakduk.restcontroller;

import com.jakduk.common.RestError;
import com.jakduk.exception.RepositoryExistException;
import com.jakduk.exception.SampleException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Created by pyohwan on 16. 3. 4.
 */

@ControllerAdvice(value = "com.jakduk.restcontroller")
public class RestExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public RestError runtimeException(RuntimeException e) {
        RestError restError = new RestError(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
        return  restError;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestError illegalArgumentException(SampleException e) {
        RestError restError = new RestError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return  restError;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public RestError noSuchElementException(SampleException e) {
        RestError restError = new RestError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return  restError;
    }

    @ExceptionHandler(RepositoryExistException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public RestError repositoryExistException(RepositoryExistException e) {
        RestError restError = new RestError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return  restError;
    }


}

