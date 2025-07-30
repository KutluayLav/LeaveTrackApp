package com.kutluayulutas.leavetrack.mapper;

import com.kutluayulutas.leavetrack.dto.response.LeaveDTO;
import com.kutluayulutas.leavetrack.model.Leave;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaveMapper {

    public LeaveDTO toDTO(Leave leave) {
        if (leave == null) {
            return null;
        }

        return LeaveDTO.builder()
                .id(leave.getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .workDays(leave.getWorkDays())
                .year(leave.getYear())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .user(leave.getUser() != null ? UserMapper.toDTO(leave.getUser()) : null)
                .department(leave.getDepartment() != null ? DepartmentMapper.toDTO(leave.getDepartment()) : null)
                .build();
    }

    public List<LeaveDTO> toDTOList(List<Leave> leaves) {
        if (leaves == null) {
            return null;
        }

        return leaves.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 