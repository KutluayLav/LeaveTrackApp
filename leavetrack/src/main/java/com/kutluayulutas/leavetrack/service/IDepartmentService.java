package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.dto.request.DepartmentRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.DepartmentDTO;
import com.kutluayulutas.leavetrack.dto.response.UserSummaryDTO;
import com.kutluayulutas.leavetrack.model.Department;

import java.util.List;

public interface IDepartmentService {

    DepartmentDTO getDepartmentById(Long id);
    DepartmentDTO createDepartment(DepartmentRequestDTO departmentRequestDTO);
    void deleteDepartment(Long id);
    List<DepartmentDTO> getAllDepartments();

    List<UserSummaryDTO> getUsersByDepartmentId(Long departmentId);
}
