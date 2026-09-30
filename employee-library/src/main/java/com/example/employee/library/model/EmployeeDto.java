package com.example.employee.library.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class EmployeeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Department department;
    private BigDecimal salary;
    private LocalDate joiningDate;

    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String firstName, String lastName, String email,
                       Department department, BigDecimal salary, LocalDate joiningDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.joiningDate = joiningDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeDto other)) {
            return false;
        }
        return Objects.equals(id, other.id)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(lastName, other.lastName)
                && Objects.equals(email, other.email)
                && department == other.department
                && Objects.equals(salary, other.salary)
                && Objects.equals(joiningDate, other.joiningDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, department, salary, joiningDate);
    }

    @Override
    public String toString() {
        return "EmployeeDto{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName
                + "', email='" + email + "', department=" + department + ", salary=" + salary
                + ", joiningDate=" + joiningDate + "}";
    }
}
