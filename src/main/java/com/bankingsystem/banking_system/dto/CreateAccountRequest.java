package com.bankingsystem.banking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(checking|savings|business)$", 
             message = "Account type must be one of: checking, savings, business")
    private String accountType;
    
    // Constructor
    public CreateAccountRequest() {}
    
    public CreateAccountRequest(String accountType) {
        this.accountType = accountType;
    }
    
    // Getters and Setters
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
