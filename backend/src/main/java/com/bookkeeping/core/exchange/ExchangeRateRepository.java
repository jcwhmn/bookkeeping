package com.bookkeeping.core.exchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    List<ExchangeRate> findByUserId(Long userId);
    Optional<ExchangeRate> findByUserIdAndTargetCurrency(Long userId, String targetCurrency);
    Optional<ExchangeRate> findByUserIdAndTargetCurrencyAndCustomTrue(Long userId, String targetCurrency);
    long countByUserId(Long userId);
}