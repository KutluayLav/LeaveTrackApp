package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.dto.request.LeaveRequestDTO;
import com.kutluayulutas.leavetrack.dto.request.LeaveUpdateRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveSummaryDTO;
import com.kutluayulutas.leavetrack.model.LeaveStatus;
import com.kutluayulutas.leavetrack.model.LeaveType;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ILeaveService {

    LeaveDTO createLeave(LeaveRequestDTO request, String email);

    LeaveDTO updateLeave(Long leaveId, LeaveUpdateRequestDTO request);

    LeaveDTO getLeaveById(Long leaveId);

    List<LeaveDTO> getAllLeaves();

    List<LeaveDTO> getLeavesByUserId(String userId);

    List<LeaveDTO> getLeavesByDepartmentId(Long departmentId);

    List<LeaveDTO> getLeavesByDateRange(LocalDate startDate, LocalDate endDate);

    List<LeaveDTO> getLeavesByStatus(LeaveStatus status);

    List<LeaveDTO> getLeavesByType(LeaveType leaveType);

    List<LeaveDTO> getLeavesWithFilters(String search, String status, String departmentId,
                                       String leaveType, LocalDate startDate, LocalDate endDate,
                                       String userId, int page, int size);

    // Async methods
    CompletableFuture<List<LeaveDTO>> getLeavesWithFiltersAsync(String search, String status, String departmentId,
                                                               String leaveType, LocalDate startDate, LocalDate endDate,
                                                               String userId, int page, int size);

    CompletableFuture<LeaveSummaryDTO> getLeaveSummaryByEmailAsync(String email, Integer year);

    CompletableFuture<List<LeaveDTO>> getAllLeavesAsync();

    CompletableFuture<List<LeaveDTO>> getLeavesByUserIdAsync(String userId);

    LeaveSummaryDTO getLeaveSummaryByUserId(String userId, Integer year);

    LeaveSummaryDTO getLeaveSummaryByEmail(String email, Integer year);

    void deleteLeave(Long leaveId);

    @PreAuthorize("hasRole('ADMIN')")
    LeaveDTO approveLeave(Long leaveId);

    @PreAuthorize("hasRole('ADMIN')")
    LeaveDTO rejectLeave(Long leaveId);

    Integer calculateWorkDays(LocalDate startDate, LocalDate endDate);

    Boolean checkLeaveLimit(String userId, Integer year, Integer requestedDays);
} 