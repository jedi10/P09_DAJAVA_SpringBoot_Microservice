package com.mediscreen.ui.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NoteCrudException extends RuntimeException {

    public NoteCrudException(String message){
        super(message);
    }
}
