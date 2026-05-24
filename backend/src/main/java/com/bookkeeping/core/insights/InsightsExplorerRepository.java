package com.bookkeeping.core.insights;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsightsExplorerRepository extends JpaRepository<InsightsExplorer, Long> {
    List<InsightsExplorer> findByUserIdOrderByDisplayOrderAsc(Long userId);
    Optional<InsightsExplorer> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}