package com.bookkeeping.core.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionTemplateRepository extends JpaRepository<TransactionTemplate, Long> {
    List<TransactionTemplate> findByUserIdOrderByDisplayOrderAsc(Long userId);
    List<TransactionTemplate> findByUserIdAndTemplateTypeOrderByDisplayOrderAsc(Long userId, Integer templateType);
    Optional<TransactionTemplate> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}