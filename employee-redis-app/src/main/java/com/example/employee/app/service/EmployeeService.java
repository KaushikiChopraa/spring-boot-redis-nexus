package com.example.employee.app.service;

import com.example.employee.app.config.CacheNames;
import com.example.employee.app.entity.EmployeeEntity;
import com.example.employee.app.exception.DuplicateEmailException;
import com.example.employee.app.mapper.EmployeeMapper;
import com.example.employee.app.repository.EmployeeRepository;
import com.example.employee.library.exception.EmployeeNotFoundException;
import com.example.employee.library.model.Department;
import com.example.employee.library.model.EmployeeDto;
import com.example.employee.library.util.EmployeeUtils;
import com.example.employee.library.validation.EmployeeValidator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Caching(
            put = @CachePut(cacheNames = CacheNames.EMPLOYEE, key = "#result.id"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.EMPLOYEE_LIST, key = "'all'"),
                    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, allEntries = true)
            })
    public EmployeeDto create(EmployeeDto request) {
        EmployeeValidator.requireValid(request);
        String email = EmployeeUtils.normalizeEmail(request.getEmail());
        if (repository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        EmployeeEntity saved = repository.save(EmployeeMapper.toEntity(request));
        log.info("DATABASE WRITE - created employee {}", saved.getId());
        return EmployeeMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEE, key = "#id")
    public EmployeeDto getById(Long id) {
        log.info("CACHE MISS - loading employee {} from database", id);
        return repository.findById(id)
                .map(EmployeeMapper::toDto)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEE_LIST, key = "'all'")
    public List<EmployeeDto> getAll() {
        log.info("CACHE MISS - loading all employees from database");
        List<EmployeeDto> employees = new ArrayList<>();
        for (EmployeeEntity entity : repository.findAll(Sort.by("id"))) {
            employees.add(EmployeeMapper.toDto(entity));
        }
        return employees;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, key = "#department.name()")
    public List<EmployeeDto> getByDepartment(Department department) {
        log.info("CACHE MISS - loading {} employees from database", department);
        List<EmployeeDto> employees = new ArrayList<>();
        for (EmployeeEntity entity : repository.findByDepartmentOrderByLastNameAsc(department)) {
            employees.add(EmployeeMapper.toDto(entity));
        }
        return employees;
    }

    @Transactional
    @Caching(
            put = @CachePut(cacheNames = CacheNames.EMPLOYEE, key = "#id"),
            evict = {
                    @CacheEvict(cacheNames = CacheNames.EMPLOYEE_LIST, key = "'all'"),
                    @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, allEntries = true)
            })
    public EmployeeDto update(Long id, EmployeeDto request) {
        EmployeeValidator.requireValid(request);
        EmployeeEntity entity = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        String email = EmployeeUtils.normalizeEmail(request.getEmail());
        if (repository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }
        EmployeeMapper.apply(request, entity);
        EmployeeEntity saved = repository.save(entity);
        log.info("DATABASE WRITE - updated employee {}", id);
        return EmployeeMapper.toDto(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.EMPLOYEE, key = "#id"),
            @CacheEvict(cacheNames = CacheNames.EMPLOYEE_LIST, key = "'all'"),
            @CacheEvict(cacheNames = CacheNames.EMPLOYEES_BY_DEPARTMENT, allEntries = true)
    })
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("DATABASE WRITE - deleted employee {}", id);
    }
}
