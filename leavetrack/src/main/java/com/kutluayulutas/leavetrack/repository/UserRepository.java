package com.kutluayulutas.leavetrack.repository;

import com.kutluayulutas.leavetrack.dto.response.UserSummaryDTO;
import com.kutluayulutas.leavetrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    boolean existsByEmail(String email);

    @Query("SELECT new com.kutluayulutas.leavetrack.dto.response.UserSummaryDTO(u.id, u.firstName, u.lastName) " +
            "FROM User u WHERE u.department.id = :departmentId")
    List<UserSummaryDTO> findUserSummariesByDepartmentId(@Param("departmentId") Long departmentId);


}
