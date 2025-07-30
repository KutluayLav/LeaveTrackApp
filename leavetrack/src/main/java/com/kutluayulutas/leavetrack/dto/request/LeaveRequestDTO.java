package com.kutluayulutas.leavetrack.dto.request;

import com.kutluayulutas.leavetrack.model.LeaveType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {

    @NotNull(message = "Başlangıç tarihi zorunludur")
    @FutureOrPresent(message = "Başlangıç tarihi bugün veya gelecek bir tarih olmalıdır")
    private LocalDate startDate;

    @NotNull(message = "Bitiş tarihi zorunludur")
    @FutureOrPresent(message = "Bitiş tarihi bugün veya gelecek bir tarih olmalıdır")
    private LocalDate endDate;

    private String reason;

    @NotNull(message = "İzin türü zorunludur")
    private LeaveType leaveType;

    private Long departmentId;
} 