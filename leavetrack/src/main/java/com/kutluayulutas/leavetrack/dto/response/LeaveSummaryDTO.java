package com.kutluayulutas.leavetrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveSummaryDTO {

    private Integer totalWorkDays;
    private Integer usedWorkDays;
    private Integer remainingWorkDays;
    private Integer year;
    private String userId;
    private String userName;
    private String departmentName;
    private Boolean isLimitExceeded;
} 