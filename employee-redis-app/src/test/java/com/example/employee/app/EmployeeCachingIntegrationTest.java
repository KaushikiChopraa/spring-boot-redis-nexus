package com.example.employee.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.employee.app.config.CacheNames;
import com.example.employee.app.entity.EmployeeEntity;
import com.example.employee.app.exception.DuplicateEmailException;
import com.example.employee.app.repository.EmployeeRepository;
import com.example.employee.app.service.EmployeeService;
import com.example.employee.library.exception.EmployeeNotFoundException;
import com.example.employee.library.exception.InvalidEmployeeException;
import com.example.employee.library.model.Department;
import com.example.employee.library.model.EmployeeDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@SpringBootTest(properties = {"spring.cache.type=simple", "app.seed.enabled=false"})
class EmployeeCachingIntegrationTest {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetState() {
        repository.deleteAll();
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    private Cache cache(String name) {
        return Objects.requireNonNull(cacheManager.getCache(name));
    }

    private EmployeeDto request(String firstName, String email) {
        return new EmployeeDto(null, firstName, "Tester", email, Department.ENGINEERING,
                new BigDecimal("50000"), LocalDate.of(2020, 1, 15));
    }

    private void changeFirstNameDirectlyInDatabase(Long id, String firstName) {
        EmployeeEntity entity = repository.findById(id).orElseThrow();
        entity.setFirstName(firstName);
        repository.save(entity);
    }

    @Test
    void cacheableReadsAreServedFromCacheUntilEvicted() {
        EmployeeDto created = service.create(request("Asha", "asha@example.com"));
        changeFirstNameDirectlyInDatabase(created.getId(), "Changed");

        assertThat(service.getById(created.getId()).getFirstName()).isEqualTo("Asha");

        cache(CacheNames.EMPLOYEE).evict(created.getId());

        assertThat(service.getById(created.getId()).getFirstName()).isEqualTo("Changed");
    }

    @Test
    void cachePutRefreshesCacheOnUpdate() {
        EmployeeDto created = service.create(request("Asha", "asha@example.com"));
        service.getById(created.getId());

        service.update(created.getId(), request("Updated", "asha@example.com"));
        changeFirstNameDirectlyInDatabase(created.getId(), "Direct");

        assertThat(service.getById(created.getId()).getFirstName()).isEqualTo("Updated");
    }

    @Test
    void cacheEvictRemovesEntryOnDelete() {
        EmployeeDto created = service.create(request("Asha", "asha@example.com"));
        service.getById(created.getId());
        assertThat(cache(CacheNames.EMPLOYEE).get(created.getId())).isNotNull();

        service.delete(created.getId());

        assertThat(cache(CacheNames.EMPLOYEE).get(created.getId())).isNull();
        assertThatThrownBy(() -> service.getById(created.getId()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void listCacheIsEvictedWhenAnEmployeeIsCreated() {
        service.create(request("Asha", "asha@example.com"));
        assertThat(service.getAll()).hasSize(1);
        assertThat(cache(CacheNames.EMPLOYEE_LIST).get("all")).isNotNull();

        service.create(request("Rohan", "rohan@example.com"));

        assertThat(cache(CacheNames.EMPLOYEE_LIST).get("all")).isNull();
        assertThat(service.getAll()).hasSize(2);
    }

    @Test
    void departmentCacheIsClearedWhenDataChanges() {
        service.create(request("Asha", "asha@example.com"));
        assertThat(service.getByDepartment(Department.ENGINEERING)).hasSize(1);
        assertThat(cache(CacheNames.EMPLOYEES_BY_DEPARTMENT).get("ENGINEERING")).isNotNull();

        service.create(request("Rohan", "rohan@example.com"));

        assertThat(cache(CacheNames.EMPLOYEES_BY_DEPARTMENT).get("ENGINEERING")).isNull();
        assertThat(service.getByDepartment(Department.ENGINEERING)).hasSize(2);
    }

    @Test
    void invalidEmployeeIsRejectedByLibraryValidator() {
        assertThatThrownBy(() -> service.create(new EmployeeDto()))
                .isInstanceOf(InvalidEmployeeException.class);
    }

    @Test
    void duplicateEmailIsRejected() {
        service.create(request("Asha", "asha@example.com"));

        assertThatThrownBy(() -> service.create(request("Other", "ASHA@example.com")))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
