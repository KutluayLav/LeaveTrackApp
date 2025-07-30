package com.kutluayulutas.leavetrack.exception;

public class LeaveNotFoundException extends RuntimeException {
    private final Long leaveId;

    public LeaveNotFoundException(String message, Long leaveId) {
        super(message);
        this.leaveId = leaveId;
    }

    public LeaveNotFoundException(String message) {
        super(message);
        this.leaveId = null;
    }

    public Long getLeaveId() {
        return leaveId;
    }
} 