package com.kutluayulutas.leavetrack.controller;

import com.kutluayulutas.leavetrack.dto.request.LeaveRequestDTO;
import com.kutluayulutas.leavetrack.dto.request.LeaveUpdateRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveSummaryDTO;
import com.kutluayulutas.leavetrack.dto.response.SuccessResponse;
import com.kutluayulutas.leavetrack.model.LeaveStatus;
import com.kutluayulutas.leavetrack.model.LeaveType;
import com.kutluayulutas.leavetrack.service.ILeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.kutluayulutas.leavetrack.model.User;
import com.kutluayulutas.leavetrack.repository.UserRepository;
import com.kutluayulutas.leavetrack.exception.NotFoundException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveController {

    private final ILeaveService leaveService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<LeaveDTO>> createLeave(@Valid @RequestBody LeaveRequestDTO request,
                                                               Authentication authentication) {
        String email = authentication.getName();
        LeaveDTO createdLeave = leaveService.createLeave(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse.<LeaveDTO>builder()
                        .message("Leave request created successfully")
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .data(createdLeave)
                        .build()
        );
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{leaveId}")
    public ResponseEntity<SuccessResponse<LeaveDTO>> updateLeave(@PathVariable Long leaveId,
                                                               @Valid @RequestBody LeaveUpdateRequestDTO request) {
        LeaveDTO updatedLeave = leaveService.updateLeave(leaveId, request);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveDTO>builder()
                        .message("Leave request updated successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(updatedLeave)
                        .build()
        );
    }

    @GetMapping("/{leaveId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<LeaveDTO>> getLeaveById(@PathVariable Long leaveId) {
        LeaveDTO leave = leaveService.getLeaveById(leaveId);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveDTO>builder()
                        .message("Leave request retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leave)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getAllLeaves() {
        List<LeaveDTO> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("All leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesWithFilters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<LeaveDTO> leaves = leaveService.getLeavesWithFilters(
            search, status, departmentId, leaveType, startDate, endDate, userId, page, size);
        
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Filtered leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/filter/async")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<SuccessResponse<List<LeaveDTO>>>> getLeavesWithFiltersAsync(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return leaveService.getLeavesWithFiltersAsync(
            search, status, departmentId, leaveType, startDate, endDate, userId, page, size)
            .thenApply(leaves -> ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Filtered leave requests retrieved asynchronously")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
            ));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String userId = user.getId();
        List<LeaveDTO> leaves = leaveService.getLeavesByUserId(userId);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("User's leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByUserId(@PathVariable String userId) {
        List<LeaveDTO> leaves = leaveService.getLeavesByUserId(userId);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("User's leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByDepartment(@PathVariable Long departmentId) {
        List<LeaveDTO> leaves = leaveService.getLeavesByDepartmentId(departmentId);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Department leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeaveDTO> leaves = leaveService.getLeavesByDateRange(startDate, endDate);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Date range leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }


    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByStatus(@PathVariable LeaveStatus status) {
        List<LeaveDTO> leaves = leaveService.getLeavesByStatus(status);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Status-based leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }

    @GetMapping("/type/{leaveType}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<LeaveDTO>>> getLeavesByType(@PathVariable LeaveType leaveType) {
        List<LeaveDTO> leaves = leaveService.getLeavesByType(leaveType);
        return ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("Type-based leave requests retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
        );
    }


    @GetMapping("/summary")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<LeaveSummaryDTO>> getLeaveSummary(Authentication authentication,
                                                                          @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year) {
        String email = authentication.getName();
        LeaveSummaryDTO summary = leaveService.getLeaveSummaryByEmail(email, year);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveSummaryDTO>builder()
                        .message("Leave summary retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(summary)
                        .build()
        );
    }

    @GetMapping("/summary/async")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<SuccessResponse<LeaveSummaryDTO>>> getLeaveSummaryAsync(
            Authentication authentication,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year) {
        
        String email = authentication.getName();
        return leaveService.getLeaveSummaryByEmailAsync(email, year)
            .thenApply(summary -> ResponseEntity.ok(
                SuccessResponse.<LeaveSummaryDTO>builder()
                        .message("Leave summary retrieved asynchronously")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(summary)
                        .build()
            ));
    }

    @GetMapping("/all/async")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<SuccessResponse<List<LeaveDTO>>>> getAllLeavesAsync() {
        
        return leaveService.getAllLeavesAsync()
            .thenApply(leaves -> ResponseEntity.ok(
                SuccessResponse.<List<LeaveDTO>>builder()
                        .message("All leaves retrieved asynchronously")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(leaves)
                        .build()
            ));
    }

    @GetMapping("/summary/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<LeaveSummaryDTO>> getLeaveSummaryByUserId(@PathVariable String userId,
                                                                                 @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year) {
        LeaveSummaryDTO summary = leaveService.getLeaveSummaryByUserId(userId, year);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveSummaryDTO>builder()
                        .message("User leave summary retrieved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(summary)
                        .build()
        );
    }

    @DeleteMapping("/{leaveId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteLeave(@PathVariable Long leaveId) {
        leaveService.deleteLeave(leaveId);
        return ResponseEntity.ok(
                SuccessResponse.<Void>builder()
                        .message("Leave deleted successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @PutMapping("/{leaveId}/approve")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<LeaveDTO>> approveLeave(@PathVariable Long leaveId) {
        LeaveDTO approvedLeave = leaveService.approveLeave(leaveId);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveDTO>builder()
                        .message("Leave approved successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(approvedLeave)
                        .build()
        );
    }


    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<LeaveDTO>> rejectLeave(@PathVariable Long leaveId) {
        LeaveDTO rejectedLeave = leaveService.rejectLeave(leaveId);
        return ResponseEntity.ok(
                SuccessResponse.<LeaveDTO>builder()
                        .message("Leave rejected successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(rejectedLeave)
                        .build()
        );
    }


    @GetMapping("/calculate-workdays")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Integer>> calculateWorkDays(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Integer workDays = leaveService.calculateWorkDays(startDate, endDate);
        return ResponseEntity.ok(
                SuccessResponse.<Integer>builder()
                        .message("Work days calculation completed successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(workDays)
                        .build()
        );
    }

    
    @GetMapping("/check-limit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> checkLeaveLimit(Authentication authentication,
                                                                 @RequestParam Integer year,
                                                                 @RequestParam Integer requestedDays) {
        String userId = authentication.getName();
        Boolean isWithinLimit = leaveService.checkLeaveLimit(userId, year, requestedDays);
        return ResponseEntity.ok(
                SuccessResponse.<Boolean>builder()
                        .message("Limit check completed successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(isWithinLimit)
                        .build()
        );
    }
} 