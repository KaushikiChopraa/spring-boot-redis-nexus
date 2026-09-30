package com.example.employee.app.seed;

import com.example.employee.app.entity.EmployeeEntity;
import com.example.employee.app.repository.EmployeeRepository;
import com.example.employee.library.model.Department;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final EmployeeRepository repository;

    public DataSeeder(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                employee("Asha", "Verma", "asha.verma@example.com", Department.ENGINEERING, "95000", "2019-04-01"),
                employee("Rohan", "Mehta", "rohan.mehta@example.com", Department.ENGINEERING, "72000", "2021-08-16"),
                employee("Neha", "Kapoor", "neha.kapoor@example.com", Department.HR, "48000", "2020-01-10"),
                employee("Vikram", "Singh", "vikram.singh@example.com", Department.FINANCE, "135000", "2015-11-23"),
                employee("Priya", "Nair", "priya.nair@example.com", Department.SALES, "36000", "2024-02-05")));
        log.info("Seeded {} employees", repository.count());
    }

    private EmployeeEntity employee(String firstName, String lastName, String email,
                                    Department department, String salary, String joiningDate) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmail(email);
        entity.setDepartment(department);
        entity.setSalary(new BigDecimal(salary));
        entity.setJoiningDate(LocalDate.parse(joiningDate));
        return entity;
    }
}
