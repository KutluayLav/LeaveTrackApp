package com.kutluayulutas.leavetrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummaryDTO {
    private String id;
    private String firstName;
    private String lastName;
}
