package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.dto.CreateAccountRequest;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.service.AccountService;
import com.bankingsystem.banking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = {"http://localhost:4200", "https://baking-system-frontend.vercel.app"})
public class AccountController {
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        List<Account> accounts = accountService.getUserAccounts(user.getId());
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable String id, Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        
        if (!accountService.isAccountOwner(id, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request, 
                                         Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            
            Account newAccount = accountService.createAccount(user.getId(), request.getAccountType());
            
            return ResponseEntity.ok(Map.of(
                "account", newAccount,
                "message", "Account created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/by-number/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber, Authentication auth) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            User user = userService.getUserByEmail(auth.getName());
            
            if (account.getUserId().equals(user.getId())) {
                return ResponseEntity.ok(account);
            } else {
                // Other's account - return only basic info for transfer validation
                return ResponseEntity.ok(Map.of(
                    "accountNumber", account.getAccountNumber(),
                    "accountType", account.getAccountType(),
                    "exists", true
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "exists", false,
                "error", "Account not found"
            ));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getAccountsSummary(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName());
            List<Account> accounts = accountService.getUserAccounts(user.getId());
            
            double totalBalance = accounts.stream()
                    .mapToDouble(acc -> acc.getBalance().doubleValue())
                    .sum();
            
            return ResponseEntity.ok(Map.of(
                "totalAccounts", accounts.size(),
                "totalBalance", totalBalance,
                "accounts", accounts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
