package com.example.employee.app.controller;

import com.example.employee.app.dto.ApiError;
import com.example.employee.app.exception.DuplicateEmailException;
import com.example.employee.library.exception.EmployeeNotFoundException;
import com.example.employee.library.exception.InvalidEmployeeException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EmployeeNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidEmployeeException.class)
    public ResponseEntity<ApiError> handleInvalid(InvalidEmployeeException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getErrors());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEmailException ex) {
        return build(HttpStatus.CONFLICT, List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST,
                List.of("Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST,
                List.of("Malformed request body. Check field types and the department value."));
    }

    private ResponseEntity<ApiError> build(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(ApiError.of(status, messages));
    }
}
