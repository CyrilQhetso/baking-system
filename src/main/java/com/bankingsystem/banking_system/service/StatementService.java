package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.dto.StatementData;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class StatementService {
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    public byte[] generateMonthlyStatement(String accountId, int year, int month, String userEmail) {
        // Verify account ownership
        Account account = accountService.getAccountById(accountId);
        User user = userService.getUserByEmail(userEmail);
        
        if (!account.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied: Account does not belong to user");
        }
        
        // Calculate statement period
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime periodStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime periodEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // Get transactions for the period
        List<Transaction> transactions = transactionService.getMonthlyTransactions(accountId, periodStart, periodEnd);
        
        // Calculate opening balance (balance at the start of the month)
        BigDecimal openingBalance = calculateOpeningBalance(accountId, periodStart);
        
        // Closing balance is the current account balance
        BigDecimal closingBalance = account.getBalance();
        
        // Create statement data
        StatementData statementData = new StatementData(
            user, account, transactions, openingBalance, closingBalance,
            periodStart, periodEnd, year, month
        );
        
        // Generate PDF
        return pdfGenerationService.generateStatementPdf(statementData);
    }
    
    private BigDecimal calculateOpeningBalance(String accountId, LocalDateTime periodStart) {
        // Get all transactions before the period start
        List<Transaction> allTransactions = transactionService.getTransactionHistory(accountId);
        
        BigDecimal openingBalance = BigDecimal.ZERO;
        
        for (Transaction transaction : allTransactions) {
            if (transaction.getTimestamp().isBefore(periodStart)) {
                if (accountId.equals(transaction.getToAccountId())) {
                    // Credit transaction
                    openingBalance = openingBalance.add(transaction.getAmount());
                } else if (accountId.equals(transaction.getFromAccountId())) {
                    // Debit transaction
                    openingBalance = openingBalance.subtract(transaction.getAmount());
                }
            }
        }
        
        return openingBalance;
    }
}
