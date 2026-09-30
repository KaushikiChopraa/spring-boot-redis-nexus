package com.example.employee.app.dto;

import com.example.employee.library.model.Department;
import com.example.employee.library.model.SalaryBand;

public record EmployeeSummary(
        Long id,
        String fullName,
        String email,
        Department department,
        SalaryBand salaryBand,
        int yearsOfService) {
}
