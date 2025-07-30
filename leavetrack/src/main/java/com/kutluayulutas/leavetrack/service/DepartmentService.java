package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.dto.request.DepartmentRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.DepartmentDTO;
import com.kutluayulutas.leavetrack.dto.response.UserSummaryDTO;
import com.kutluayulutas.leavetrack.exception.NotFoundException;
import com.kutluayulutas.leavetrack.mapper.DepartmentMapper;
import com.kutluayulutas.leavetrack.model.Department;
import com.kutluayulutas.leavetrack.repository.DepartmentRepository;
import com.kutluayulutas.leavetrack.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
        return DepartmentMapper.toDTO(department);
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentRequestDTO requestDTO) {
        Department department = DepartmentMapper.toEntity(requestDTO);
        Department saved = departmentRepository.save(department);
        return DepartmentMapper.toDTO(saved);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found with id: " + id));
        departmentRepository.delete(department);
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return DepartmentMapper.toDTOList(departments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getUsersByDepartmentId(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException("Department not found with id: " + departmentId);
        }
        return userRepository.findUserSummariesByDepartmentId(departmentId);
    }


}
