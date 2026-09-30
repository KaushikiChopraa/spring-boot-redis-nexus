package com.example.employee.library.validation;

import com.example.employee.library.exception.InvalidEmployeeException;
import com.example.employee.library.model.EmployeeDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class EmployeeValidator {

    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 120;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private EmployeeValidator() {
    }

    public static List<String> validate(EmployeeDto employee) {
        List<String> errors = new ArrayList<>();
        if (employee == null) {
            errors.add("Employee details are required");
            return errors;
        }
        validateName("firstName", employee.getFirstName(), errors);
        validateName("lastName", employee.getLastName(), errors);
        validateEmail(employee.getEmail(), errors);
        if (employee.getDepartment() == null) {
            errors.add("department is required");
        }
        BigDecimal salary = employee.getSalary();
        if (salary == null) {
            errors.add("salary is required");
        } else if (salary.signum() <= 0) {
            errors.add("salary must be greater than zero");
        }
        LocalDate joiningDate = employee.getJoiningDate();
        if (joiningDate == null) {
            errors.add("joiningDate is required");
        } else if (joiningDate.isAfter(LocalDate.now())) {
            errors.add("joiningDate cannot be in the future");
        }
        return errors;
    }

    public static boolean isValid(EmployeeDto employee) {
        return validate(employee).isEmpty();
    }

    public static void requireValid(EmployeeDto employee) {
        List<String> errors = validate(employee);
        if (!errors.isEmpty()) {
            throw new InvalidEmployeeException(errors);
        }
    }

    private static void validateName(String field, String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required");
        } else if (value.trim().length() > MAX_NAME_LENGTH) {
            errors.add(field + " must not exceed " + MAX_NAME_LENGTH + " characters");
        }
    }

    private static void validateEmail(String email, List<String> errors) {
        if (email == null || email.isBlank()) {
            errors.add("email is required");
        } else if (email.trim().length() > MAX_EMAIL_LENGTH) {
            errors.add("email must not exceed " + MAX_EMAIL_LENGTH + " characters");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.add("email is not a valid email address");
        }
    }
}
