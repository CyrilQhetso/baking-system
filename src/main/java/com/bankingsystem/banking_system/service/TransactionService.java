package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.dto.TransactionRequest;
import com.bankingsystem.banking_system.dto.TransferRequest;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Transactional
    public Transaction deposit(TransactionRequest request) {
        Account account = accountService.getAccountById(request.getAccountId());
        
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountService.saveAccount(account);
        
        Transaction transaction = new Transaction();
        transaction.setToAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Deposit");
        transaction.setCategory(request.getCategory());
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction withdraw(TransactionRequest request) {
        Account account = accountService.getAccountById(request.getAccountId());
        
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountService.saveAccount(account);
        
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Withdrawal");
        transaction.setCategory(request.getCategory());
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction transfer(TransactionRequest request) {
        Account fromAccount = accountService.getAccountById(request.getAccountId());
        Account toAccount = accountService.getAccountById(request.getToAccountId());
        
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        
        accountService.saveAccount(fromAccount);
        accountService.saveAccount(toAccount);
        
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription(request.getDescription() != null ? 
                                 request.getDescription() : "Transfer to " + toAccount.getAccountNumber());
        transaction.setCategory(request.getCategory());
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction transferByAccountNumber(TransferRequest request) {
        Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
        Account toAccount = accountService.getAccountByNumberForTransfer(request.getToAccountNumber());
        
        // Prevent self-transfer
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        
        accountService.saveAccount(fromAccount);
        accountService.saveAccount(toAccount);
        
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccount.getId());
        transaction.setToAccountId(toAccount.getId());
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription(request.getDescription() != null ? 
                                 request.getDescription() : "Transfer to " + toAccount.getAccountNumber());
        transaction.setCategory(request.getCategory());
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction transferBetweenOwnAccounts(String fromAccountId, String toAccountId, 
                                                BigDecimal amount, String description, String userId) {
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);
        
        // Verify both accounts belong to the user
        if (!fromAccount.getUserId().equals(userId) || !toAccount.getUserId().equals(userId)) {
            throw new RuntimeException("Both accounts must belong to the same user");
        }
        
        // Prevent self-transfer
        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        accountService.saveAccount(fromAccount);
        accountService.saveAccount(toAccount);
        
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setDescription(description != null ? description : 
                                 "Transfer from " + fromAccount.getAccountType() + " to " + toAccount.getAccountType());
        transaction.setCategory("Internal Transfer");
        
        return transactionRepository.save(transaction);
    }
    
    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    public List<Transaction> getMonthlyTransactions(String accountId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByAccountIdAndTimestampBetween(accountId, start, end);
    }
    
    public List<Transaction> getAllUserTransactions(String userId) {
        List<Account> userAccounts = accountService.getUserAccounts(userId);
        return userAccounts.stream()
                .flatMap(account -> getTransactionHistory(account.getId()).stream())
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .toList();
    }
}
