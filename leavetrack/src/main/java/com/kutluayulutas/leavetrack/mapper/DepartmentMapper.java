package com.kutluayulutas.leavetrack.mapper;

import com.kutluayulutas.leavetrack.dto.response.DepartmentDTO;
import com.kutluayulutas.leavetrack.dto.request.DepartmentRequestDTO;
import com.kutluayulutas.leavetrack.model.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class DepartmentMapper {

    private DepartmentMapper() {}

    public static DepartmentDTO toDTO(Department department) {
        if (department == null) return null;
        return new DepartmentDTO(department.getId(), department.getName());
    }

    public static List<DepartmentDTO> toDTOList(List<Department> departments) {
        return departments.stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static Department toEntity(DepartmentRequestDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        return department;
    }
}
