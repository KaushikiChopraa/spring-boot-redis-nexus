package com.example.employee.library.util;

import com.example.employee.library.model.EmployeeDto;
import com.example.employee.library.model.SalaryBand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

public final class EmployeeUtils {

    public static final BigDecimal ENTRY_LIMIT = new BigDecimal("40000");
    public static final BigDecimal MID_LIMIT = new BigDecimal("100000");

    private EmployeeUtils() {
    }

    public static String fullName(EmployeeDto employee) {
        String first = employee.getFirstName() == null ? "" : employee.getFirstName().trim();
        String last = employee.getLastName() == null ? "" : employee.getLastName().trim();
        return (first + " " + last).trim();
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public static int yearsOfService(EmployeeDto employee, LocalDate asOf) {
        if (employee.getJoiningDate() == null || asOf == null || asOf.isBefore(employee.getJoiningDate())) {
            return 0;
        }
        return Period.between(employee.getJoiningDate(), asOf).getYears();
    }

    public static int yearsOfService(EmployeeDto employee) {
        return yearsOfService(employee, LocalDate.now());
    }

    public static SalaryBand salaryBand(BigDecimal salary) {
        if (salary == null || salary.compareTo(ENTRY_LIMIT) < 0) {
            return SalaryBand.ENTRY;
        }
        if (salary.compareTo(MID_LIMIT) < 0) {
            return SalaryBand.MID;
        }
        return SalaryBand.SENIOR;
    }
}
