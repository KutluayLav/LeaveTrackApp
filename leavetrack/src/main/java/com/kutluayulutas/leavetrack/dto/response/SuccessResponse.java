package com.kutluayulutas.leavetrack.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class SuccessResponse<T> {

    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private T data;

}
