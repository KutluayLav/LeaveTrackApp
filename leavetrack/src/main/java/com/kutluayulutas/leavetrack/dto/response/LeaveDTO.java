package com.kutluayulutas.leavetrack.dto.response;

import com.kutluayulutas.leavetrack.model.LeaveStatus;
import com.kutluayulutas.leavetrack.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveType leaveType;
    private LeaveStatus status;
    private Integer workDays;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO user;
    private DepartmentDTO department;
} 