package com.example.employee.app.controller;

import com.example.employee.app.dto.EmployeeSummary;
import com.example.employee.app.service.EmployeeService;
import com.example.employee.library.model.Department;
import com.example.employee.library.model.EmployeeDto;
import com.example.employee.library.util.EmployeeUtils;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@RequestBody EmployeeDto request) {
        EmployeeDto created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<EmployeeDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/department/{department}")
    public List<EmployeeDto> getByDepartment(@PathVariable Department department) {
        return service.getByDepartment(department);
    }

    @GetMapping("/{id}/summary")
    public EmployeeSummary getSummary(@PathVariable Long id) {
        EmployeeDto employee = service.getById(id);
        return new EmployeeSummary(
                employee.getId(),
                EmployeeUtils.fullName(employee),
                employee.getEmail(),
                employee.getDepartment(),
                EmployeeUtils.salaryBand(employee.getSalary()),
                EmployeeUtils.yearsOfService(employee));
    }

    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable Long id, @RequestBody EmployeeDto request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
