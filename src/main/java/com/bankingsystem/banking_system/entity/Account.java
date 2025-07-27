package com.bankingsystem.banking_system.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @Indexed(unique = true)
    private String accountNumber;
    
    @NotNull(message = "Balance is required")
    private BigDecimal balance;
    
    private String accountType;

    @Indexed(unique = true)
    private String cardNumber;

    private String expiryDate;

    private String cvv;

    private LocalDateTime createdAt;
    
    public Account() {
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public String getUserId() { 
        return userId; 
    }
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    public String getAccountNumber() { 
        return accountNumber; 
    }
    public void setAccountNumber(String accountNumber) { 
        this.accountNumber = accountNumber; 
    }
    public BigDecimal getBalance() { 
        return balance; 
    }
    public void setBalance(BigDecimal balance) { 
        this.balance = balance; 
    }
    public String getAccountType() { 
        return accountType; 
    }
    public void setAccountType(String accountType) { 
        this.accountType = accountType; 
    }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getCvv() {
        return cvv;
    }
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
