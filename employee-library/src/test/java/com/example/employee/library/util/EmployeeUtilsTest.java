package com.example.employee.library.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.employee.library.model.Department;
import com.example.employee.library.model.EmployeeDto;
import com.example.employee.library.model.SalaryBand;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmployeeUtilsTest {

    private EmployeeDto employee(LocalDate joiningDate) {
        return new EmployeeDto(1L, " Asha ", " Verma ", "Asha@Example.com",
                Department.HR, new BigDecimal("50000"), joiningDate);
    }

    @Test
    void buildsFullName() {
        assertEquals("Asha Verma", EmployeeUtils.fullName(employee(LocalDate.of(2020, 1, 1))));
    }

    @Test
    void normalizesEmail() {
        assertEquals("asha@example.com", EmployeeUtils.normalizeEmail("  Asha@Example.com "));
        assertNull(EmployeeUtils.normalizeEmail(null));
    }

    @Test
    void calculatesYearsOfService() {
        EmployeeDto employee = employee(LocalDate.of(2020, 3, 15));
        assertEquals(5, EmployeeUtils.yearsOfService(employee, LocalDate.of(2025, 3, 15)));
        assertEquals(4, EmployeeUtils.yearsOfService(employee, LocalDate.of(2025, 3, 14)));
        assertEquals(0, EmployeeUtils.yearsOfService(employee, LocalDate.of(2019, 1, 1)));
    }

    @Test
    void classifiesSalaryBands() {
        assertEquals(SalaryBand.ENTRY, EmployeeUtils.salaryBand(new BigDecimal("39999.99")));
        assertEquals(SalaryBand.MID, EmployeeUtils.salaryBand(new BigDecimal("40000")));
        assertEquals(SalaryBand.MID, EmployeeUtils.salaryBand(new BigDecimal("99999")));
        assertEquals(SalaryBand.SENIOR, EmployeeUtils.salaryBand(new BigDecimal("100000")));
    }
}
