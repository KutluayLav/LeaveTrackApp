package com.kutluayulutas.leavetrack.mapper;

import com.kutluayulutas.leavetrack.dto.response.DepartmentDTO;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;
import com.kutluayulutas.leavetrack.model.User;

public class UserMapper {


    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        DepartmentDTO departmentDTO = null;
        if (user.getDepartment() != null) {
            departmentDTO = DepartmentDTO.builder()
                    .id(user.getDepartment().getId())
                    .name(user.getDepartment().getName())
                    .build();
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .department(departmentDTO)
                .authorities(user.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .collect(java.util.stream.Collectors.toSet()))
                .lastLoginDate(user.getLastLoginDate() != null ? user.getLastLoginDate().toString() : null)
                .build();
    }

}
