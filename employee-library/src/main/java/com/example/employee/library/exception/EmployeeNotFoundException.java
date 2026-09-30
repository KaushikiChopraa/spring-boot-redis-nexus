package com.example.employee.library.exception;

public class EmployeeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Long employeeId;

    public EmployeeNotFoundException(Long employeeId) {
        super("Employee not found with id " + employeeId);
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
}
