package com.bankingsystem.banking_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankingsystem.banking_system.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    @Query("{'$or': [{'fromAccountId': ?0}, {'toAccountId': ?0}]}")
    List<Transaction> findByAccountId(String accountId);
    
    @Query("{'$or': [{'fromAccountId': ?0}, {'toAccountId': ?0}], 'timestamp': {'$gte': ?1, '$lte': ?2}}")
    List<Transaction> findByAccountIdAndTimestampBetween(String accountId, LocalDateTime start, LocalDateTime end);
}
