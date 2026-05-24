package com.bookkeeping.core.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserIdOrderByLastSeenDesc(Long userId);
    Optional<Token> findByTokenHashAndUserId(String tokenHash, Long userId);
    long countByUserId(Long userId);

    @Modifying
    @Query("UPDATE Token t SET t.isCurrent = false WHERE t.userId = :userId")
    void clearCurrentForUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.tokenHash = :hash AND t.userId = :userId")
    int deleteByTokenHashAndUserId(@Param("hash") String hash, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.id = :id AND t.userId = :userId")
    void deleteByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.userId = :userId AND t.tokenHash != :excludeHash")
    void deleteByUserIdExcept(@Param("userId") Long userId, @Param("excludeHash") String excludeHash);
}