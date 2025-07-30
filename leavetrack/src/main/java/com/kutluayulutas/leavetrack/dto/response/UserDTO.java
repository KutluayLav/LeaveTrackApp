package com.kutluayulutas.leavetrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private DepartmentDTO department;
    private Set<String> authorities;
    private String lastLoginDate;
}
