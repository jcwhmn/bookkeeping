package com.bookkeeping.core.exchange;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final SecurityUtils securityUtils;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, SecurityUtils securityUtils) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.securityUtils = securityUtils;
    }

    private static final Map<String, String> DEFAULT_RATES = Map.ofEntries(
            Map.entry("USD", "1.0"),
            Map.entry("EUR", "0.92"),
            Map.entry("GBP", "0.79"),
            Map.entry("JPY", "149.50"),
            Map.entry("CNY", "7.24"),
            Map.entry("AUD", "1.53"),
            Map.entry("CAD", "1.36"),
            Map.entry("CHF", "0.88"),
            Map.entry("HKD", "7.82"),
            Map.entry("SGD", "1.34"),
            Map.entry("INR", "83.12"),
            Map.entry("KRW", "1320.50"),
            Map.entry("MXN", "17.15"),
            Map.entry("BRL", "4.97"),
            Map.entry("RUB", "91.50")
    );

    @Transactional(readOnly = true)
    public LatestRatesResponse getLatestRates() {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<ExchangeRate> customRates = exchangeRateRepository.findByUserId(userId);
        List<ExchangeRateItem> items = DEFAULT_RATES.entrySet().stream().map(e -> {
            String currency = e.getKey();
            String defaultRate = e.getValue();
            String rate = customRates.stream()
                    .filter(r -> r.getTargetCurrency().equals(currency))
                    .findFirst()
                    .map(r -> r.getRate())
                    .orElse(defaultRate);
            boolean isCustom = customRates.stream()
                    .anyMatch(r -> r.getTargetCurrency().equals(currency));
            return new ExchangeRateItem(currency, rate, isCustom);
        }).toList();
        return new LatestRatesResponse(items, Instant.now().getEpochSecond());
    }

    @Transactional
    public void updateCustomRate(String currency, String rate) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ExchangeRate existing = exchangeRateRepository.findByUserIdAndTargetCurrency(userId, currency)
                .orElse(null);
        if (existing != null) {
            exchangeRateRepository.save(existing.toBuilder().rate(rate).build());
        } else {
            ExchangeRate newRate = ExchangeRate.builder()
                    .userId(userId)
                    .baseCurrency("USD")
                    .targetCurrency(currency)
                    .rate(rate)
                    .custom(true)
                    .build();
            exchangeRateRepository.save(newRate);
        }
    }

    @Transactional
    public void deleteCustomRate(String currency) {
        Long userId = securityUtils.requireCurrentUser().getId();
        exchangeRateRepository.findByUserIdAndTargetCurrency(userId, currency)
                .ifPresent(exchangeRateRepository::delete);
    }

    public record LatestRatesResponse(List<ExchangeRateItem> items, long updateTime) {}
    public record ExchangeRateItem(String currency, String rate, boolean custom) {}
    public record CustomRateRequest(String currency, String rate) {}
}