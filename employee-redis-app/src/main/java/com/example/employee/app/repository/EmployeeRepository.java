package com.example.employee.app.repository;

import com.example.employee.app.entity.EmployeeEntity;
import com.example.employee.library.model.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findByDepartmentOrderByLastNameAsc(Department department);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
