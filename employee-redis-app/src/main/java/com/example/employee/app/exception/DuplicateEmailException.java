package com.example.employee.app.exception;

public class DuplicateEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateEmailException(String email) {
        super("An employee with email " + email + " already exists");
    }
}
