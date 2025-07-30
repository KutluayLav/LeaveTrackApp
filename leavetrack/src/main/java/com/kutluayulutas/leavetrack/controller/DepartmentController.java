package com.kutluayulutas.leavetrack.controller;

import com.kutluayulutas.leavetrack.dto.request.DepartmentRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.DepartmentDTO;
import com.kutluayulutas.leavetrack.dto.response.SuccessResponse;
import com.kutluayulutas.leavetrack.dto.response.UserSummaryDTO;
import com.kutluayulutas.leavetrack.service.DepartmentService;
import com.kutluayulutas.leavetrack.service.IDepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final IDepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<DepartmentDTO>> createDepartment(@RequestBody DepartmentRequestDTO request) {
        DepartmentDTO response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.<DepartmentDTO>builder()
                        .message("Department created successfully")
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<DepartmentDTO>>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(
                SuccessResponse.<List<DepartmentDTO>>builder()
                        .message("Departments retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(departments)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(
                SuccessResponse.<DepartmentDTO>builder()
                        .message("Department retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(
                SuccessResponse.<Void>builder()
                        .message("Department deleted successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<SuccessResponse<List<UserSummaryDTO>>> getUsersByDepartmentId(@PathVariable Long id) {
        List<UserSummaryDTO> users = departmentService.getUsersByDepartmentId(id);
        return ResponseEntity.ok(
                SuccessResponse.<List<UserSummaryDTO>>builder()
                        .message("Users retrieved successfully for department id: " + id)
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(users)
                        .build()
        );
    }

}
