package com.mediscreen.patient.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
