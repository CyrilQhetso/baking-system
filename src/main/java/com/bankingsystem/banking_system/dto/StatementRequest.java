package com.bankingsystem.banking_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StatementRequest {

    @NotBlank(message = "Account ID is required")
    private String accountId;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be at least 2000")
    @Max(value = 2100, message = "Year must be at most 2100")
    private Integer year;
    
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;
    
    // Getters and Setters
    public String getAccountId() { 
        return accountId; 
    }
    public void setAccountId(String accountId) { 
        this.accountId = accountId; 
    }
    
    public Integer getYear() { 
        return year; 
    }
    public void setYear(Integer year) { 
        this.year = year; 
    }
    
    public Integer getMonth() { 
        return month; 
    }
    public void setMonth(Integer month) { 
        this.month = month; 
    }
}
