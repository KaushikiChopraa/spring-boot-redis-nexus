package com.example.employee.library.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.employee.library.exception.InvalidEmployeeException;
import com.example.employee.library.model.Department;
import com.example.employee.library.model.EmployeeDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmployeeValidatorTest {

    private EmployeeDto validEmployee() {
        return new EmployeeDto(null, "Asha", "Verma", "asha.verma@example.com",
                Department.ENGINEERING, new BigDecimal("75000"), LocalDate.of(2021, 6, 1));
    }

    @Test
    void acceptsValidEmployee() {
        assertTrue(EmployeeValidator.isValid(validEmployee()));
        assertTrue(EmployeeValidator.validate(validEmployee()).isEmpty());
    }

    @Test
    void rejectsNullEmployee() {
        assertEquals(1, EmployeeValidator.validate(null).size());
    }

    @Test
    void reportsEveryMissingField() {
        assertEquals(6, EmployeeValidator.validate(new EmployeeDto()).size());
    }

    @Test
    void rejectsInvalidEmail() {
        EmployeeDto employee = validEmployee();
        employee.setEmail("not-an-email");
        assertFalse(EmployeeValidator.isValid(employee));
    }

    @Test
    void rejectsNonPositiveSalary() {
        EmployeeDto employee = validEmployee();
        employee.setSalary(BigDecimal.ZERO);
        assertTrue(EmployeeValidator.validate(employee).contains("salary must be greater than zero"));
    }

    @Test
    void rejectsFutureJoiningDate() {
        EmployeeDto employee = validEmployee();
        employee.setJoiningDate(LocalDate.now().plusDays(1));
        assertTrue(EmployeeValidator.validate(employee).contains("joiningDate cannot be in the future"));
    }

    @Test
    void requireValidThrowsWithAllErrors() {
        EmployeeDto employee = validEmployee();
        employee.setFirstName(" ");
        employee.setEmail("bad");
        InvalidEmployeeException ex =
                assertThrows(InvalidEmployeeException.class, () -> EmployeeValidator.requireValid(employee));
        assertEquals(2, ex.getErrors().size());
    }
}
