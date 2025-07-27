package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.dto.InternalTransferRequest;
import com.bankingsystem.banking_system.dto.TransactionRequest;
import com.bankingsystem.banking_system.dto.TransferRequest;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.service.AccountService;
import com.bankingsystem.banking_system.service.TransactionService;
import com.bankingsystem.banking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = {"http://localhost:4200", "https://baking-system-frontend.vercel.app"})
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            if (!accountService.isAccountOwner(request.getAccountId(), user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            Transaction transaction = transactionService.deposit(request);
            return ResponseEntity.ok(Map.of("transaction", transaction, "message", "Deposit successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            if (!accountService.isAccountOwner(request.getAccountId(), user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            Transaction transaction = transactionService.withdraw(request);
            return ResponseEntity.ok(Map.of("transaction", transaction, "message", "Withdrawal successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            if (!accountService.isAccountOwner(request.getAccountId(), user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            Transaction transaction = transactionService.transfer(request);
            return ResponseEntity.ok(Map.of("transaction", transaction, "message", "Transfer successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/transfer-by-account-number")
    public ResponseEntity<?> transferByAccountNumber(@Valid @RequestBody TransferRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            
            // Verify user owns the from account
            Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
            if (!fromAccount.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You don't own the source account"));
            }
            
            Transaction transaction = transactionService.transferByAccountNumber(request);
            return ResponseEntity.ok(Map.of("transaction", transaction, "message", "Transfer successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/internal-transfer")
    public ResponseEntity<?> internalTransfer(@Valid @RequestBody InternalTransferRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            
            Transaction transaction = transactionService.transferBetweenOwnAccounts(
                request.getFromAccountId(), 
                request.getToAccountId(), 
                request.getAmount(), 
                request.getDescription(), 
                user.getId()
            );
            
            return ResponseEntity.ok(Map.of("transaction", transaction, "message", "Internal transfer successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getTransactionHistory(@RequestParam String accountId, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            if (!accountService.isAccountOwner(accountId, user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            List<Transaction> transactions = transactionService.getTransactionHistory(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllUserTransactions(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            List<Transaction> transactions = transactionService.getAllUserTransactions(user.getId());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentTransactions(@RequestParam(defaultValue = "10") int limit, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            List<Transaction> allTransactions = transactionService.getAllUserTransactions(user.getId());
            
            // Get the most recent transactions up to the limit
            List<Transaction> recentTransactions = allTransactions.stream()
                    .limit(limit)
                    .toList();
            
            return ResponseEntity.ok(recentTransactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
