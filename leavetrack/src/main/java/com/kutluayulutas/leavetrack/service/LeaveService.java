package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.config.LeaveConfig;
import com.kutluayulutas.leavetrack.dto.request.LeaveRequestDTO;
import com.kutluayulutas.leavetrack.dto.request.LeaveUpdateRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveSummaryDTO;
import com.kutluayulutas.leavetrack.exception.InvalidDateRangeException;
import com.kutluayulutas.leavetrack.exception.LeaveLimitExceededException;
import com.kutluayulutas.leavetrack.exception.LeaveNotFoundException;
import com.kutluayulutas.leavetrack.exception.NotFoundException;
import com.kutluayulutas.leavetrack.mapper.LeaveMapper;
import com.kutluayulutas.leavetrack.model.Department;
import com.kutluayulutas.leavetrack.model.Leave;
import com.kutluayulutas.leavetrack.model.LeaveStatus;
import com.kutluayulutas.leavetrack.model.LeaveType;
import com.kutluayulutas.leavetrack.model.User;
import com.kutluayulutas.leavetrack.repository.DepartmentRepository;
import com.kutluayulutas.leavetrack.repository.LeaveRepository;
import com.kutluayulutas.leavetrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService implements ILeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveMapper leaveMapper;
    private final LeaveConfig leaveConfig;

    @Override
    public LeaveDTO createLeave(LeaveRequestDTO request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Tarih kontrolü
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new InvalidDateRangeException(
                "Start date (" + request.getStartDate() + ") cannot be after end date (" + request.getEndDate() + "). Please select a valid date range.",
                request.getStartDate(),
                request.getEndDate()
            );
        }

        // Geçmiş tarih kontrolü
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException(
                "Start date cannot be in the past. Please select today or a future date.",
                request.getStartDate(),
                request.getEndDate()
            );
        }

        Integer workDays = calculateWorkDays(request.getStartDate(), request.getEndDate());

        // İzin limiti kontrolü
        if (leaveConfig.getEnableLeaveLimitCheck()) {
            Integer usedWorkDays = leaveRepository.calculateTotalWorkDaysByUserIdAndYear(user.getId(), request.getStartDate().getYear());
            Integer totalRequested = usedWorkDays + workDays;
            
            if (totalRequested > leaveConfig.getMaxYearlyLeaveDays()) {
                throw new LeaveLimitExceededException(
                    String.format("Annual leave limit exceeded. Used: %d days, Requested: %d days, Total: %d days. Maximum %d days allowed. Remaining leave balance: %d days.",
                        usedWorkDays, workDays, totalRequested, leaveConfig.getMaxYearlyLeaveDays(), 
                        Math.max(0, leaveConfig.getMaxYearlyLeaveDays() - usedWorkDays)),
                    leaveConfig.getMaxYearlyLeaveDays(),
                    usedWorkDays,
                    workDays
                );
            }
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found"));
        }

        Leave leave = Leave.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .leaveType(request.getLeaveType())
                .status(LeaveStatus.PENDING)
                .workDays(workDays)
                .year(request.getStartDate().getYear())
                .user(user)
                .department(department != null ? department : user.getDepartment())
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return leaveMapper.toDTO(savedLeave);
    }

    @Override
    public LeaveDTO updateLeave(Long leaveId, LeaveUpdateRequestDTO request) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found", leaveId));

        // Tarih kontrolü
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new InvalidDateRangeException(
                    "Start date (" + request.getStartDate() + ") cannot be after end date (" + request.getEndDate() + "). Please select a valid date range.",
                    request.getStartDate(),
                    request.getEndDate()
                );
            }

            // Geçmiş tarih kontrolü (sadece onaylanmamış izinler için)
            if (leave.getStatus() == LeaveStatus.PENDING && request.getStartDate().isBefore(LocalDate.now())) {
                throw new InvalidDateRangeException(
                    "Start date cannot be in the past. Please select today or a future date.",
                    request.getStartDate(),
                    request.getEndDate()
                );
            }
        }

        if (request.getStartDate() != null) {
            leave.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            leave.setEndDate(request.getEndDate());
        }
        if (request.getReason() != null) {
            leave.setReason(request.getReason());
        }
        if (request.getLeaveType() != null) {
            leave.setLeaveType(request.getLeaveType());
        }
        if (request.getStatus() != null) {
            leave.setStatus(request.getStatus());
        }
        if (request.getWorkDays() != null) {
            leave.setWorkDays(request.getWorkDays());
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            leave.setWorkDays(calculateWorkDays(request.getStartDate(), request.getEndDate()));
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found"));
            leave.setDepartment(department);
        }

        Leave updatedLeave = leaveRepository.save(leave);
        return leaveMapper.toDTO(updatedLeave);
    }

    @Override
    public LeaveDTO getLeaveById(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));
        return leaveMapper.toDTO(leave);
    }

    @Override
    public List<LeaveDTO> getAllLeaves() {
        List<Leave> leaves = leaveRepository.findAll();
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    public List<LeaveDTO> getLeavesByUserId(String userId) {
        List<Leave> leaves = leaveRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    public List<LeaveDTO> getLeavesByDepartmentId(Long departmentId) {
        List<Leave> leaves = leaveRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId);
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    public List<LeaveDTO> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Leave> leaves = leaveRepository.findByStartDateBetweenOrEndDateBetweenOrderByCreatedAtDesc(
                startDate, endDate, startDate, endDate);
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    public List<LeaveDTO> getLeavesByStatus(LeaveStatus status) {
        List<Leave> leaves = leaveRepository.findByStatusOrderByCreatedAtDesc(status);
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    public List<LeaveDTO> getLeavesByType(LeaveType leaveType) {
        List<Leave> leaves = leaveRepository.findByLeaveTypeOrderByCreatedAtDesc(leaveType);
        return leaveMapper.toDTOList(leaves);
    }

    @Override
    @Async
    public CompletableFuture<List<LeaveDTO>> getLeavesWithFiltersAsync(String search, String status, String departmentId,
                                                                      String leaveType, LocalDate startDate, LocalDate endDate,
                                                                      String userId, int page, int size) {
        log.info("Starting async leave filtering with search: {}, status: {}, departmentId: {}", search, status, departmentId);
        
        List<Leave> allLeaves = leaveRepository.findAll();
        
        List<LeaveDTO> result = allLeaves.stream()
            .filter(leave -> {
                // Arama filtresi
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    String userName = (leave.getUser().getFirstName() + " " + leave.getUser().getLastName()).toLowerCase();
                    String reason = leave.getReason() != null ? leave.getReason().toLowerCase() : "";
                    if (!userName.contains(searchLower) && !reason.contains(searchLower)) {
                        return false;
                    }
                }
                
                // Durum filtresi
                if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                    if (!leave.getStatus().toString().equals(status)) {
                        return false;
                    }
                }
                
                // Departman filtresi
                if (departmentId != null && !departmentId.trim().isEmpty() && !departmentId.equals("all")) {
                    if (leave.getDepartment() == null || !leave.getDepartment().getId().toString().equals(departmentId)) {
                        return false;
                    }
                }
                
                // İzin türü filtresi
                if (leaveType != null && !leaveType.trim().isEmpty() && !leaveType.equals("all")) {
                    if (!leave.getLeaveType().toString().equals(leaveType)) {
                        return false;
                    }
                }
                
                // Tarih aralığı filtresi
                if (startDate != null) {
                    if (leave.getStartDate().isBefore(startDate)) {
                        return false;
                    }
                }
                if (endDate != null) {
                    if (leave.getEndDate().isAfter(endDate)) {
                        return false;
                    }
                }
                
                // Kullanıcı filtresi
                if (userId != null && !userId.trim().isEmpty()) {
                    if (!leave.getUser().getId().equals(userId)) {
                        return false;
                    }
                }
                
                return true;
            })
            .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
            .skip(page * size)
            .limit(size)
            .map(leaveMapper::toDTO)
            .collect(Collectors.toList());
        
        log.info("Async leave filtering completed. Found {} results", result.size());
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public List<LeaveDTO> getLeavesWithFilters(String search, String status, String departmentId,
                                              String leaveType, LocalDate startDate, LocalDate endDate,
                                              String userId, int page, int size) {
        
        List<Leave> allLeaves = leaveRepository.findAll();
        
        return allLeaves.stream()
            .filter(leave -> {

                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    String userName = (leave.getUser().getFirstName() + " " + leave.getUser().getLastName()).toLowerCase();
                    String reason = leave.getReason() != null ? leave.getReason().toLowerCase() : "";
                    if (!userName.contains(searchLower) && !reason.contains(searchLower)) {
                        return false;
                    }
                }
                
                if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                    if (!leave.getStatus().toString().equals(status)) {
                        return false;
                    }
                }
                

                if (departmentId != null && !departmentId.trim().isEmpty() && !departmentId.equals("all")) {
                    if (leave.getDepartment() == null || !leave.getDepartment().getId().toString().equals(departmentId)) {
                        return false;
                    }
                }
                

                if (leaveType != null && !leaveType.trim().isEmpty() && !leaveType.equals("all")) {
                    if (!leave.getLeaveType().toString().equals(leaveType)) {
                        return false;
                    }
                }
                

                if (startDate != null) {
                    if (leave.getStartDate().isBefore(startDate)) {
                        return false;
                    }
                }
                if (endDate != null) {
                    if (leave.getEndDate().isAfter(endDate)) {
                        return false;
                    }
                }
                
                // Kullanıcı filtresi
                if (userId != null && !userId.trim().isEmpty()) {
                    if (!leave.getUser().getId().equals(userId)) {
                        return false;
                    }
                }
                
                return true;
            })
            .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
            .skip(page * size)
            .limit(size)
            .map(leaveMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public LeaveSummaryDTO getLeaveSummaryByUserId(String userId, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Integer usedWorkDays = leaveRepository.calculateTotalWorkDaysByUserIdAndYear(userId, year);
        Integer remainingWorkDays = leaveConfig.getMaxYearlyLeaveDays() - usedWorkDays;
        Boolean isLimitExceeded = usedWorkDays > leaveConfig.getMaxYearlyLeaveDays();

        return LeaveSummaryDTO.builder()
                .totalWorkDays(leaveConfig.getMaxYearlyLeaveDays())
                .usedWorkDays(usedWorkDays)
                .remainingWorkDays(Math.max(0, remainingWorkDays))
                .year(year)
                .userId(userId)
                .userName(user.getFirstName() + " " + user.getLastName())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isLimitExceeded(isLimitExceeded)
                .build();
    }

    public LeaveSummaryDTO getLeaveSummaryByEmail(String email, Integer year) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String userId = user.getId();
        Integer usedWorkDays = leaveRepository.calculateTotalWorkDaysByUserIdAndYear(userId, year);
        Integer remainingWorkDays = leaveConfig.getMaxYearlyLeaveDays() - usedWorkDays;
        Boolean isLimitExceeded = usedWorkDays > leaveConfig.getMaxYearlyLeaveDays();
        return LeaveSummaryDTO.builder()
                .totalWorkDays(leaveConfig.getMaxYearlyLeaveDays())
                .usedWorkDays(usedWorkDays)
                .remainingWorkDays(Math.max(0, remainingWorkDays))
                .year(year)
                .userId(userId)
                .userName(user.getFirstName() + " " + user.getLastName())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isLimitExceeded(isLimitExceeded)
                .build();
    }

    @Override
    public void deleteLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));
        leaveRepository.delete(leave);
    }

    @Override
    public LeaveDTO approveLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));
        leave.setStatus(LeaveStatus.APPROVED);
        Leave savedLeave = leaveRepository.save(leave);
        return leaveMapper.toDTO(savedLeave);
    }

    @Override
    public LeaveDTO rejectLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));
        leave.setStatus(LeaveStatus.REJECTED);
        Leave savedLeave = leaveRepository.save(leave);
        return leaveMapper.toDTO(savedLeave);
    }

    @Override
    public Integer calculateWorkDays(LocalDate startDate, LocalDate endDate) {
        if (leaveConfig.getEnableWorkDayCalculation()) {
            
            int workDays = 0;
            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && 
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    workDays++;
                }
                currentDate = currentDate.plusDays(1);
            }
            return workDays;
        } else {
            
            return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        }
    }

    @Override
    public Boolean checkLeaveLimit(String userId, Integer year, Integer requestedDays) {
        Integer usedWorkDays = leaveRepository.calculateTotalWorkDaysByUserIdAndYear(userId, year);
        return (usedWorkDays + requestedDays) <= leaveConfig.getMaxYearlyLeaveDays();
    }

    @Async
    public CompletableFuture<LeaveSummaryDTO> getLeaveSummaryByEmailAsync(String email, Integer year) {
        log.info("Starting async leave summary calculation for email: {} and year: {}", email, year);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String userId = user.getId();
        Integer usedWorkDays = leaveRepository.calculateTotalWorkDaysByUserIdAndYear(userId, year);
        Integer remainingWorkDays = leaveConfig.getMaxYearlyLeaveDays() - usedWorkDays;
        Boolean isLimitExceeded = usedWorkDays > leaveConfig.getMaxYearlyLeaveDays();
        
        LeaveSummaryDTO result = LeaveSummaryDTO.builder()
                .totalWorkDays(leaveConfig.getMaxYearlyLeaveDays())
                .usedWorkDays(usedWorkDays)
                .remainingWorkDays(Math.max(0, remainingWorkDays))
                .year(year)
                .userId(userId)
                .userName(user.getFirstName() + " " + user.getLastName())
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .isLimitExceeded(isLimitExceeded)
                .build();
        
        log.info("Async leave summary calculation completed for user: {}", user.getFirstName());
        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<List<LeaveDTO>> getAllLeavesAsync() {
        log.info("Starting async retrieval of all leaves");
        
        List<Leave> leaves = leaveRepository.findAll();
        List<LeaveDTO> result = leaveMapper.toDTOList(leaves);
        
        log.info("Async retrieval of all leaves completed. Found {} leaves", result.size());
        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<List<LeaveDTO>> getLeavesByUserIdAsync(String userId) {
        log.info("Starting async retrieval of leaves for user: {}", userId);
        
        List<Leave> leaves = leaveRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<LeaveDTO> result = leaveMapper.toDTOList(leaves);
        
        log.info("Async retrieval of user leaves completed. Found {} leaves for user: {}", result.size(), userId);
        return CompletableFuture.completedFuture(result);
    }
} 