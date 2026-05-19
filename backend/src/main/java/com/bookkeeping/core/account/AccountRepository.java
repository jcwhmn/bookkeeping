package com.bookkeeping.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    List<Account> findAllByUser(Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findById(Long id);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.id = :id")
    Optional<Account> findByUserAndId(Long userId, Long id);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.archived = false ORDER BY a.name ASC")
    List<Account> findActiveByUser(Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.name = :name")
    Optional<Account> findByUserAndName(Long userId, String name);
    
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.userId = :userId AND a.includeInTotal = true")
    Long sumBalanceByUser(Long userId);
}