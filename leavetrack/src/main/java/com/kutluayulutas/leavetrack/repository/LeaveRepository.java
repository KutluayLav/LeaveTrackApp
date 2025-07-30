package com.kutluayulutas.leavetrack.repository;

import com.kutluayulutas.leavetrack.model.Leave;
import com.kutluayulutas.leavetrack.model.LeaveStatus;
import com.kutluayulutas.leavetrack.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Kullanıcının izinlerini getir
    List<Leave> findByUserIdOrderByCreatedAtDesc(String userId);

    // Kullanıcının belirli yıldaki izinlerini getir
    List<Leave> findByUserIdAndYearOrderByCreatedAtDesc(String userId, Integer year);

    // Departmana göre izinleri getir
    List<Leave> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    // Tarih aralığına göre izinleri getir
    List<Leave> findByStartDateBetweenOrEndDateBetweenOrderByCreatedAtDesc(
            LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2);

    // Kullanıcının belirli yıldaki onaylanmış izinlerini getir
    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.year = :year AND l.status = 'APPROVED'")
    List<Leave> findApprovedLeavesByUserIdAndYear(@Param("userId") String userId, @Param("year") Integer year);

    // Kullanıcının belirli yıldaki toplam iş günü sayısını hesapla
    @Query("SELECT COALESCE(SUM(l.workDays), 0) FROM Leave l WHERE l.user.id = :userId AND l.year = :year AND l.status = 'APPROVED'")
    Integer calculateTotalWorkDaysByUserIdAndYear(@Param("userId") String userId, @Param("year") Integer year);

    // Departman ve tarih aralığına göre izinleri getir
    @Query("SELECT l FROM Leave l WHERE l.department.id = :departmentId AND " +
           "(l.startDate BETWEEN :startDate AND :endDate OR l.endDate BETWEEN :startDate AND :endDate)")
    List<Leave> findByDepartmentIdAndDateRange(@Param("departmentId") Long departmentId, 
                                              @Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);

    // İzin türüne göre izinleri getir
    List<Leave> findByLeaveTypeOrderByCreatedAtDesc(LeaveType leaveType);

    // Duruma göre izinleri getir
    List<Leave> findByStatusOrderByCreatedAtDesc(LeaveStatus status);

    // Kullanıcının belirli tarih aralığındaki izinlerini getir
    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND " +
           "(l.startDate BETWEEN :startDate AND :endDate OR l.endDate BETWEEN :startDate AND :endDate)")
    List<Leave> findByUserIdAndDateRange(@Param("userId") String userId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
} 