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

    // === Cursor-based pagination ===
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime < :cursor " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findByUserIdBeforeCursor(@Param("userId") Long userId,
                                                @Param("cursor") Long cursor,
                                                Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime > :cursor " +
           "ORDER BY t.transactionTime ASC")
    List<Transaction> findByUserIdAfterCursor(@Param("userId") Long userId,
                                                @Param("cursor") Long cursor,
                                                Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime >= :startTime AND t.transactionTime < :endTime")
    long countByUserIdAndMonth(@Param("userId") Long userId,
                               @Param("startTime") Long startTime,
                               @Param("endTime") Long endTime);

    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId);

    // === DB-level filtering ===
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.userId = :userId
        AND (:year IS NULL OR FUNCTION('DATE_PART', 'year', TO_TIMESTAMP(t.transactionTime)) = :year)
        AND (:month IS NULL OR FUNCTION('DATE_PART', 'month', TO_TIMESTAMP(t.transactionTime)) = :month)
        AND (:accountId IS NULL OR t.accountId = :accountId)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        AND (:transactionType IS NULL OR t.transactionType = :transactionType)
        AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY t.transactionTime DESC
        """)
    List<Transaction> findWithFilters(@Param("userId") Long userId,
                                       @Param("year") Integer year,
                                       @Param("month") Integer month,
                                       @Param("accountId") Long accountId,
                                       @Param("categoryId") Long categoryId,
                                       @Param("transactionType") Integer transactionType,
                                       @Param("search") String search,
                                       Pageable pageable);

    @Query("""
        SELECT COUNT(t) FROM Transaction t
        WHERE t.userId = :userId
        AND (:year IS NULL OR FUNCTION('DATE_PART', 'year', TO_TIMESTAMP(t.transactionTime)) = :year)
        AND (:month IS NULL OR FUNCTION('DATE_PART', 'month', TO_TIMESTAMP(t.transactionTime)) = :month)
        AND (:accountId IS NULL OR t.accountId = :accountId)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        AND (:transactionType IS NULL OR t.transactionType = :transactionType)
        AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    long countWithFilters(@Param("userId") Long userId,
                          @Param("year") Integer year,
                          @Param("month") Integer month,
                          @Param("accountId") Long accountId,
                          @Param("categoryId") Long categoryId,
                          @Param("transactionType") Integer transactionType,
                          @Param("search") String search);
}