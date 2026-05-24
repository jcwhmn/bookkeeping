package com.bookkeeping.core.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId, org.springframework.data.domain.Pageable pageable);
    List<Transaction> findByUserIdAndAccountIdOrderByTransactionTimeDesc(Long userId, Long accountId, org.springframework.data.domain.Pageable pageable);
    List<Transaction> findByUserIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(Long userId, Long from, Long to);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime >= :startTime AND t.transactionTime < :endTime " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findByUserIdAndMonth(@Param("userId") Long userId,
                                           @Param("startTime") Long startTime,
                                           @Param("endTime") Long endTime);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime < :cursor " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findByUserIdBeforeCursor(@Param("userId") Long userId,
                                                @Param("cursor") Long cursor,
                                                org.springframework.data.domain.Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime > :cursor " +
           "ORDER BY t.transactionTime ASC")
    List<Transaction> findByUserIdAfterCursor(@Param("userId") Long userId,
                                                @Param("cursor") Long cursor,
                                                org.springframework.data.domain.Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId " +
           "AND t.transactionTime >= :startTime AND t.transactionTime < :endTime")
    long countByUserIdAndMonth(@Param("userId") Long userId,
                               @Param("startTime") Long startTime,
                               @Param("endTime") Long endTime);

    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId);

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
                                       org.springframework.data.domain.Pageable pageable);

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

    @Modifying
    @Query("UPDATE Transaction t SET t.accountId = :newAccountId WHERE t.accountId = :oldAccountId AND t.userId = :userId")
    int moveAllTransactions(@Param("oldAccountId") Long oldAccountId,
                            @Param("newAccountId") Long newAccountId,
                            @Param("userId") Long userId);

    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.id IN :ids")
    List<Transaction> findByUserIdAndIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);
}