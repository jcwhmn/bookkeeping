package com.bookkeeping.core.exchange;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange_rates")
@Tag(name = "Exchange Rates", description = "Exchange rate management APIs")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/latest.json")
    @Operation(summary = "Get latest exchange rates")
    public ApiResponse<ExchangeRateService.LatestRatesResponse> getLatestRates() {
        return ApiResponse.success(exchangeRateService.getLatestRates());
    }

    @PostMapping("/user_custom/update.json")
    @Operation(summary = "Update user custom exchange rate")
    public ApiResponse<Void> updateCustomRate(@RequestBody ExchangeRateService.CustomRateRequest request) {
        exchangeRateService.updateCustomRate(request.currency(), request.rate());
        return ApiResponse.success(null);
    }

    @PostMapping("/user_custom/delete.json")
    @Operation(summary = "Delete user custom exchange rate")
    public ApiResponse<Void> deleteCustomRate(@RequestBody ExchangeRateService.CustomRateRequest request) {
        exchangeRateService.deleteCustomRate(request.currency());
        return ApiResponse.success(null);
    }
}