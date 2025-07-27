package com.bankingsystem.banking_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankingsystem.banking_system.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    /**
     * Find all accounts belonging to a user
     */
    List<Account> findByUserId(String userId);
    
    /**
     * Find account by account number
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Find accounts by user ID and account type
     */
    List<Account> findByUserIdAndAccountType(String userId, String accountType);
    
    /**
     * Count accounts by user ID and account type
     */
    long countByUserIdAndAccountType(String userId, String accountType);
    
    /**
     * Check if account number exists
     */
    boolean existsByAccountNumber(String accountNumber);
    
    /**
     * Check if card number exists
     */
    boolean existsByCardNumber(String cardNumber);
    
    /**
     * Find accounts by user ID ordered by creation date
     */
    List<Account> findByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * Find active accounts (you can add an 'active' field to Account entity later if needed)
     */
    @Query("{'userId': ?0}")
    List<Account> findActiveAccountsByUserId(String userId);
}
