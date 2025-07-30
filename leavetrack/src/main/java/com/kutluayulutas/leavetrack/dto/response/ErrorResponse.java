package com.kutluayulutas.leavetrack.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String path, String message, String error, LocalDateTime timestamp) {
        this.status = status;
        this.path = path;
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
    }
}
