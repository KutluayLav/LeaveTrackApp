package com.kutluayulutas.leavetrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DepartmentDTO {
    private Long id;
    private String name;
}
