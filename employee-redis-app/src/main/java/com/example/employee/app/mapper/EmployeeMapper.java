package com.example.employee.app.mapper;

import com.example.employee.app.entity.EmployeeEntity;
import com.example.employee.library.model.EmployeeDto;
import com.example.employee.library.util.EmployeeUtils;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static EmployeeDto toDto(EmployeeEntity entity) {
        return new EmployeeDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getDepartment(),
                entity.getSalary(),
                entity.getJoiningDate());
    }

    public static EmployeeEntity toEntity(EmployeeDto dto) {
        EmployeeEntity entity = new EmployeeEntity();
        apply(dto, entity);
        return entity;
    }

    public static void apply(EmployeeDto dto, EmployeeEntity entity) {
        entity.setFirstName(dto.getFirstName().trim());
        entity.setLastName(dto.getLastName().trim());
        entity.setEmail(EmployeeUtils.normalizeEmail(dto.getEmail()));
        entity.setDepartment(dto.getDepartment());
        entity.setSalary(dto.getSalary());
        entity.setJoiningDate(dto.getJoiningDate());
    }
}
