package com.example.employee.library.exception;

import java.util.List;

public class InvalidEmployeeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<String> errors;

    public InvalidEmployeeException(List<String> errors) {
        super("Invalid employee: " + String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
