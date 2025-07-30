package com.kutluayulutas.leavetrack.controller;

import com.kutluayulutas.leavetrack.config.LeaveConfig;
import com.kutluayulutas.leavetrack.dto.response.SuccessResponse;
import com.kutluayulutas.leavetrack.service.ILeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class LeaveAdminController {

    private final LeaveConfig leaveConfig;

    @PutMapping("/config/max-days")
    public ResponseEntity<SuccessResponse<Integer>> updateMaxYearlyLeaveDays(@RequestParam Integer maxDays) {
        leaveConfig.setMaxYearlyLeaveDays(maxDays);
        return ResponseEntity.ok(SuccessResponse.<Integer>builder()
                .message("Maximum yearly leave days updated to " + maxDays)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(maxDays)
                .build());
    }

    @GetMapping("/config/max-days")
    public ResponseEntity<SuccessResponse<Integer>> getMaxYearlyLeaveDays() {
        Integer maxDays = leaveConfig.getMaxYearlyLeaveDays();
        return ResponseEntity.ok(SuccessResponse.<Integer>builder()
                .message("Maximum yearly leave days retrieved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(maxDays)
                .build());
    }

    @PutMapping("/config/enable-limit-check") 
    public ResponseEntity<SuccessResponse<Boolean>> enableLeaveLimitCheck(@RequestParam Boolean enable) {
        leaveConfig.setEnableLeaveLimitCheck(enable);
        return ResponseEntity.ok(SuccessResponse.<Boolean>builder()
                .message("Leave limit check " + (enable ? "enabled" : "disabled"))
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(enable)
                .build());
    }

    @GetMapping("/config/enable-limit-check")
    public ResponseEntity<SuccessResponse<Boolean>> getLeaveLimitCheckStatus() {
        Boolean status = leaveConfig.getEnableLeaveLimitCheck();
        return ResponseEntity.ok(SuccessResponse.<Boolean>builder()
                .message("Leave limit check status retrieved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(status)
                .build());
    }

    @PutMapping("/config/enable-work-day-calculation")
    public ResponseEntity<SuccessResponse<Boolean>> enableWorkDayCalculation(@RequestParam Boolean enable) {
        leaveConfig.setEnableWorkDayCalculation(enable);
        return ResponseEntity.ok(SuccessResponse.<Boolean>builder()
                .message("Work day calculation " + (enable ? "enabled" : "disabled"))
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(enable)
                .build());
    }

    @GetMapping("/config/enable-work-day-calculation")
    public ResponseEntity<SuccessResponse<Boolean>> getWorkDayCalculationStatus() {
        Boolean status = leaveConfig.getEnableWorkDayCalculation();
        return ResponseEntity.ok(SuccessResponse.<Boolean>builder()
                .message("Work day calculation status retrieved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(status)
                .build());
    }
} 