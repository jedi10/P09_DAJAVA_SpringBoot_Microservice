package com.mediscreen.patient.exception;

public class PatientUniquenessConstraintException extends RuntimeException{

    public PatientUniquenessConstraintException(String message) {
        super(message);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
