package com.bankingsystem.banking_system.dto;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StatementData {
    private User user;
    private Account account;
    private List<Transaction> transactions;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private LocalDateTime statementPeriodStart;
    private LocalDateTime statementPeriodEnd;
    private int year;
    private int month;
    
    // Constructors
    public StatementData() {}
    
    public StatementData(User user, Account account, List<Transaction> transactions,
                        BigDecimal openingBalance, BigDecimal closingBalance,
                        LocalDateTime statementPeriodStart, LocalDateTime statementPeriodEnd,
                        int year, int month) {
        this.user = user;
        this.account = account;
        this.transactions = transactions;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.statementPeriodStart = statementPeriodStart;
        this.statementPeriodEnd = statementPeriodEnd;
        this.year = year;
        this.month = month;
    }
    
    // Getters and Setters
    public User getUser() { 
        return user; 
    }
    public void setUser(User user) { 
        this.user = user; 
    }
    
    public Account getAccount() { 
        return account; }
    public void setAccount(Account account) { 
        this.account = account; 
    }
    
    public List<Transaction> getTransactions() { 
        return transactions; 
    }
    public void setTransactions(List<Transaction> transactions) { 
        this.transactions = transactions; 
    }
    
    public BigDecimal getOpeningBalance() { 
        return openingBalance; 
    }
    public void setOpeningBalance(BigDecimal openingBalance) { 
        this.openingBalance = openingBalance; 
    }
    
    public BigDecimal getClosingBalance() { 
        return closingBalance; 
    }
    public void setClosingBalance(BigDecimal closingBalance) { 
        this.closingBalance = closingBalance; 
    }
    
    public LocalDateTime getStatementPeriodStart() { 
        return statementPeriodStart; 
    }
    public void setStatementPeriodStart(LocalDateTime statementPeriodStart) { 
        this.statementPeriodStart = statementPeriodStart; 
    }
    
    public LocalDateTime getStatementPeriodEnd() { 
        return statementPeriodEnd; 
    }
    public void setStatementPeriodEnd(LocalDateTime statementPeriodEnd) { 
        this.statementPeriodEnd = statementPeriodEnd; 
    }
    
    public int getYear() { 
        return year; 
    }
    public void setYear(int year) { 
        this.year = year; 
    }
    
    public int getMonth() { 
        return month; 
    }
    public void setMonth(int month) { 
        this.month = month; 
    }
}
