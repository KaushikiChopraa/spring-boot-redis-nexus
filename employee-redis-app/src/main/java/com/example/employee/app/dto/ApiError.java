package com.example.employee.app.dto;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;

public record ApiError(Instant timestamp, int status, String error, List<String> messages) {

    public static ApiError of(HttpStatus status, List<String> messages) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), messages);
    }
}
