package com.bookkeeping.core.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long> {

    List<ScheduledTransaction> findByUserIdAndDeletedFalse(Long userId);

    List<ScheduledTransaction> findByUserIdAndActiveTrueAndDeletedFalse(Long userId);

    Optional<ScheduledTransaction> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    @Query("SELECT s FROM ScheduledTransaction s WHERE s.active = true AND s.deleted = false AND s.nextRunTime <= :now")
    List<ScheduledTransaction> findDueForExecution(@Param("now") Long now);

    @Query("SELECT COUNT(s) FROM ScheduledTransaction s WHERE s.userId = :userId AND s.active = true AND s.deleted = false")
    long countActiveByUserId(@Param("userId") Long userId);

    // Statistics
    @Query("SELECT s.frequency, COUNT(s) FROM ScheduledTransaction s WHERE s.userId = :userId AND s.deleted = false GROUP BY s.frequency")
    List<Object[]> countByFrequency(@Param("userId") Long userId);

    // Next upcoming
    @Query("SELECT s FROM ScheduledTransaction s WHERE s.userId = :userId AND s.active = true AND s.deleted = false ORDER BY s.nextRunTime ASC")
    List<ScheduledTransaction> findUpcomingByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
}