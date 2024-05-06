package ru.edu.springdata.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalDtoStateException extends IllegalStateException {

    public IllegalDtoStateException(String message) {
        super(message);
    }
}
