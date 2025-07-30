package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.config.LeaveConfig;
import com.kutluayulutas.leavetrack.dto.request.LeaveRequestDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveDTO;
import com.kutluayulutas.leavetrack.dto.response.LeaveSummaryDTO;
import com.kutluayulutas.leavetrack.model.*;
import com.kutluayulutas.leavetrack.repository.DepartmentRepository;
import com.kutluayulutas.leavetrack.repository.LeaveRepository;
import com.kutluayulutas.leavetrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private LeaveConfig leaveConfig;

    @InjectMocks
    private LeaveService leaveService;

    private User testUser;
    private Department testDepartment;
    private LeaveRequestDTO leaveRequest;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("Test Department")
                .build();

        testUser = User.builder()
                .id("test-user-id")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .department(testDepartment)
                .authorities(new HashSet<>(Arrays.asList(Role.ROLE_USER)))
                .build();

        leaveRequest = LeaveRequestDTO.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .leaveType(LeaveType.YILLIK)
                .reason("Test leave")
                .build();

        when(leaveConfig.getMaxYearlyLeaveDays()).thenReturn(20);
        when(leaveConfig.getEnableLeaveLimitCheck()).thenReturn(true);
    }

    @Test
    void testCalculateWorkDays() {
        LocalDate startDate = LocalDate.of(2024, 1, 1); // Pazartesi
        LocalDate endDate = LocalDate.of(2024, 1, 5);   // Cuma

        Integer workDays = leaveService.calculateWorkDays(startDate, endDate);

        assertEquals(5, workDays); // 5 iş günü (Pazartesi-Cuma)
    }

    @Test
    void testCalculateWorkDaysWithWeekend() {
        LocalDate startDate = LocalDate.of(2024, 1, 1); // Pazartesi
        LocalDate endDate = LocalDate.of(2024, 1, 7);   // Pazar

        Integer workDays = leaveService.calculateWorkDays(startDate, endDate);

        assertEquals(5, workDays); // 5 iş günü (Pazartesi-Cuma, hafta sonu hariç)
    }

    @Test
    void testCheckLeaveLimit() {
        when(leaveRepository.calculateTotalWorkDaysByUserIdAndYear(anyString(), anyInt())).thenReturn(10);

        Boolean result = leaveService.checkLeaveLimit("test-user-id", 2024, 5);

        assertTrue(result); // 10 + 5 = 15 <= 20
    }

    @Test
    void testCheckLeaveLimitExceeded() {
        when(leaveRepository.calculateTotalWorkDaysByUserIdAndYear(anyString(), anyInt())).thenReturn(18);

        Boolean result = leaveService.checkLeaveLimit("test-user-id", 2024, 5);

        assertFalse(result); // 18 + 5 = 23 > 20
    }

    @Test
    void testCreateLeave() {
        when(userRepository.findById("test-user-id")).thenReturn(Optional.of(testUser));
        when(leaveRepository.save(any(Leave.class))).thenAnswer(invocation -> {
            Leave leave = invocation.getArgument(0);
            leave.setId(1L);
            return leave;
        });

        LeaveDTO result = leaveService.createLeave(leaveRequest, "test-user-id");

        assertNotNull(result);
        assertEquals(LeaveType.YILLIK, result.getLeaveType());
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        assertEquals(5, result.getWorkDays()); // 5 iş günü
    }

    @Test
    void testCreateLeaveWithInvalidDates() {
        leaveRequest.setStartDate(LocalDate.now().plusDays(5));
        leaveRequest.setEndDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            leaveService.createLeave(leaveRequest, "test-user-id");
        });
    }

    @Test
    void testGetLeaveSummary() {
        when(userRepository.findById("test-user-id")).thenReturn(Optional.of(testUser));
        when(leaveRepository.calculateTotalWorkDaysByUserIdAndYear("test-user-id", 2024)).thenReturn(10);

        LeaveSummaryDTO result = leaveService.getLeaveSummaryByUserId("test-user-id", 2024);

        assertNotNull(result);
        assertEquals(20, result.getTotalWorkDays());
        assertEquals(10, result.getUsedWorkDays());
        assertEquals(10, result.getRemainingWorkDays());
        assertEquals("Test User", result.getUserName());
        assertEquals("Test Department", result.getDepartmentName());
        assertFalse(result.getIsLimitExceeded());
    }
} 