package com.bankingsystem.banking_system.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    
    private String fromAccountId;
    private String toAccountId;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    private String description;
    private String category;
    private LocalDateTime timestamp;
    
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
    
    // Getters and Setters
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }
    
    public String getFromAccountId() { 
        return fromAccountId; 
    }
    public void setFromAccountId(String fromAccountId) { 
        this.fromAccountId = fromAccountId; 
    }
    
    public String getToAccountId() { 
        return toAccountId; 
    }
    public void setToAccountId(String toAccountId) { 
        this.toAccountId = toAccountId; 
    }
    
    public BigDecimal getAmount() { 
        return amount; 
    }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount; 
    }
    
    public TransactionType getType() { 
        return type; 
    }
    public void setType(TransactionType type) { 
        this.type = type; 
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
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
    public void setTimestamp(LocalDateTime timestamp) { 
        this.timestamp = timestamp; 
    }
}
