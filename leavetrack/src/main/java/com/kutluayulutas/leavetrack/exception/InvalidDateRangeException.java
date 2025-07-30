package com.kutluayulutas.leavetrack.exception;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public InvalidDateRangeException(String message, LocalDate startDate, LocalDate endDate) {
        super(message);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
} 