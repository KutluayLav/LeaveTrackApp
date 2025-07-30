package com.kutluayulutas.leavetrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "leave")
public class LeaveConfig {

    private Integer maxYearlyLeaveDays = 20;
    private Boolean enableWorkDayCalculation = true;
    private Boolean enableLeaveLimitCheck = true;

    public Integer getMaxYearlyLeaveDays() {
        return maxYearlyLeaveDays;
    }

    public void setMaxYearlyLeaveDays(Integer maxYearlyLeaveDays) {
        this.maxYearlyLeaveDays = maxYearlyLeaveDays;
    }

    public Boolean getEnableWorkDayCalculation() {
        return enableWorkDayCalculation;
    }

    public void setEnableWorkDayCalculation(Boolean enableWorkDayCalculation) {
        this.enableWorkDayCalculation = enableWorkDayCalculation;
    }

    public Boolean getEnableLeaveLimitCheck() {
        return enableLeaveLimitCheck;
    }

    public void setEnableLeaveLimitCheck(Boolean enableLeaveLimitCheck) {
        this.enableLeaveLimitCheck = enableLeaveLimitCheck;
    }
} 