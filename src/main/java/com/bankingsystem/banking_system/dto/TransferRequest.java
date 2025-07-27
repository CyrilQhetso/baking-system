package com.bankingsystem.banking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank(message = "From account number is required")
    private String fromAccountNumber;
    
    @NotBlank(message = "To account number is required")
    private String toAccountNumber;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String description;
    private String category;
    
    // Constructors
    public TransferRequest() {}
    
    public TransferRequest(String fromAccountNumber, String toAccountNumber, 
                          BigDecimal amount, String description, String category) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }
    
    // Getters and Setters
    public String getFromAccountNumber() { 
        return fromAccountNumber; 
    }
    public void setFromAccountNumber(String fromAccountNumber) { 
        this.fromAccountNumber = fromAccountNumber; 
    }
    
    public String getToAccountNumber() { 
        return toAccountNumber; 
    }
    public void setToAccountNumber(String toAccountNumber) { 
        this.toAccountNumber = toAccountNumber; 
    }
    
    public BigDecimal getAmount() { 
        return amount; 
    }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount; 
    }
    
    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getCategory() { 
        return category; 
    }
    public void setCategory(String category) { 
        this.category = category; 
    }
}
