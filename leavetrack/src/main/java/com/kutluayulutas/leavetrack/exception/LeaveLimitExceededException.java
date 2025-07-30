package com.kutluayulutas.leavetrack.exception;

public class LeaveLimitExceededException extends RuntimeException {
    private final Integer maxDays;
    private final Integer usedDays;
    private final Integer requestedDays;

    public LeaveLimitExceededException(String message, Integer maxDays, Integer usedDays, Integer requestedDays) {
        super(message);
        this.maxDays = maxDays;
        this.usedDays = usedDays;
        this.requestedDays = requestedDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public Integer getUsedDays() {
        return usedDays;
    }

    public Integer getRequestedDays() {
        return requestedDays;
    }
} 