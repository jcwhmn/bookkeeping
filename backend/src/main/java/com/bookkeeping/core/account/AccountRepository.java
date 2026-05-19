package com.bookkeeping.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.deleted = false ORDER BY a.createdUnixTime DESC")
    List<Account> findAllByUserNotDeleted(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.id = :id AND a.deleted = false")
    Optional<Account> findByIdNotDeleted(@Param("id") Long id);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.id = :id AND a.deleted = false")
    Optional<Account> findByUserAndIdNotDeleted(@Param("userId") Long userId, @Param("id") Long id);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.archived = false AND a.deleted = false ORDER BY a.name ASC")
    List<Account> findAllActiveByUser(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.name = :name AND a.deleted = false")
    Optional<Account> findByUserAndName(@Param("userId") Long userId, @Param("name") String name);
    
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.userId = :userId AND a.includeInTotal = true AND a.deleted = false")
    Long sumBalanceByUser(@Param("userId") Long userId);
}