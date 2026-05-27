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

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND (:year IS NULL OR EXTRACT(YEAR FROM CAST(FROM_UNIXTIME(t.transactionTime) AS timestamp)) = :year) " +
           "AND (:month IS NULL OR EXTRACT(MONTH FROM CAST(FROM_UNIXTIME(t.transactionTime) AS timestamp)) = :month) " +
           "AND (:accountId IS NULL OR t.accountId = :accountId) " +
           "AND (:categoryId IS NULL OR t.categoryId = :categoryId) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findWithFilters(@Param("userId") Long userId,
                                       @Param("year") Integer year,
                                       @Param("month") Integer month,
                                       @Param("accountId") Long accountId,
                                       @Param("categoryId") Long categoryId,
                                       @Param("transactionType") Integer transactionType,
                                       @Param("search") String search,
                                       org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId " +
           "AND (:year IS NULL OR EXTRACT(YEAR FROM CAST(FROM_UNIXTIME(t.transactionTime) AS timestamp)) = :year) " +
           "AND (:month IS NULL OR EXTRACT(MONTH FROM CAST(FROM_UNIXTIME(t.transactionTime) AS timestamp)) = :month) " +
           "AND (:accountId IS NULL OR t.accountId = :accountId) " +
           "AND (:categoryId IS NULL OR t.categoryId = :categoryId) " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
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

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.accountId = :accountId AND t.userId = :userId")
    void deleteByAccountIdAndUserId(@Param("accountId") Long accountId, @Param("userId") Long userId);

    default List<Transaction> searchWithFilters(Long userId, TransactionSearchParams params) {
        return findWithFilters(userId, params.year(), params.month(),
                params.accountId() != null ? params.accountId().longValue() : null,
                params.categoryId() != null ? params.categoryId().longValue() : null,
                params.transactionType(),
                params.search(),
                org.springframework.data.domain.Pageable.unpaged());
    }

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND (:startTime IS NULL OR t.transactionTime >= :startTime) " +
           "AND (:endTime IS NULL OR t.transactionTime < :endTime) " +
           "AND (:accountId IS NULL OR t.accountId = :accountId)")
    List<Transaction> searchTransactions(@Param("userId") Long userId,
                                         @Param("startTime") Long startTime,
                                         @Param("endTime") Long endTime,
                                         @Param("accountId") Long accountId,
                                         @Param("tagIds") List<Long> tagIds,
                                         @Param("keyword") String keyword);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId " +
           "AND (:startTime IS NULL OR t.transactionTime >= :startTime) " +
           "AND (:endTime IS NULL OR t.transactionTime < :endTime) " +
           "ORDER BY t.transactionTime DESC")
    List<Transaction> findByAccountIdAndTimeRange(@Param("accountId") Long accountId,
                                                  @Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.transactionType IN (2, 4) THEN t.amount ELSE 0 END) - " +
           "SUM(CASE WHEN t.transactionType IN (3, 5) THEN t.amount ELSE 0 END), 0) " +
           "FROM Transaction t WHERE t.accountId = :accountId " +
           "AND (:beforeTime IS NULL OR t.transactionTime < :beforeTime)")
    long sumBalanceBefore(@Param("accountId") Long accountId, @Param("beforeTime") Long beforeTime);
}