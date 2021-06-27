package com.mediscreen.patient.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)//, reason = "Patient already registered")
public class PatientUniquenessConstraintException extends RuntimeException{

    public PatientUniquenessConstraintException(String message) {
        super(message);
        log.error(message);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}


//https://www.baeldung.com/spring-response-status