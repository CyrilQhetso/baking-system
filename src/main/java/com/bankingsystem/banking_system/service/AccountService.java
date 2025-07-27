package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    
    public List<Account> getUserAccounts(String userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public Account getAccountById(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }
    
    public Account createAccount(String userId, String accountType) {
        // Validate account type
        if (!isValidAccountType(accountType)) {
            throw new RuntimeException("Invalid account type. Supported types: checking, savings, business");
        }
        
        // Check if user already has this type of account
        List<Account> existingAccounts = getUserAccounts(userId);
        long sameTypeCount = existingAccounts.stream()
                .filter(acc -> acc.getAccountType().equalsIgnoreCase(accountType))
                .count();
        
        // Allow maximum of 2 accounts per type
        if (sameTypeCount >= 2) {
            throw new RuntimeException("Maximum number of " + accountType + " accounts reached");
        }
        
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType.toLowerCase());
        account.setCardNumber(generateCardNumber());
        account.setExpiryDate(generateExpiryDate());
        account.setCvv(generateCvv());
        account.setBalance(BigDecimal.ZERO);
        
        return accountRepository.save(account);
    }
    
    public boolean isAccountOwner(String accountId, String userId) {
        try {
            Account account = getAccountById(accountId);
            return account.getUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    public Account getAccountByNumberForTransfer(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));
    }
    
    private boolean isValidAccountType(String accountType) {
        return accountType != null && 
               (accountType.equalsIgnoreCase("checking") || 
                accountType.equalsIgnoreCase("savings") || 
                accountType.equalsIgnoreCase("business"));
    }
    
    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append("4"); // Visa prefix
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateExpiryDate() {
        Random random = new Random();
        LocalDate futureDate = LocalDate.now().plusYears(3 + random.nextInt(3));
        return futureDate.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}
