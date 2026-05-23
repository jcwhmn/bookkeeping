package com.bookkeeping.core.transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId, Pageable pageable);
    List<Transaction> findByUserIdAndAccountIdOrderByTransactionTimeDesc(Long userId, Long accountId, Pageable pageable);
    List<Transaction> findByUserIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(Long userId, Long from, Long to);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime >= :startTime AND t.transactionTime < :endTime " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findByUserIdAndMonth(@Param("userId") Long userId, 
                                           @Param("startTime") Long startTime, 
                                           @Param("endTime") Long endTime);
}