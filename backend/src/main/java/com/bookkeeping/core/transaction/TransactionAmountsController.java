package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionAmountsController {

    private final TransactionAmountsService amountsService;
    private final SecurityUtils securityUtils;

    public TransactionAmountsController(TransactionAmountsService amountsService, SecurityUtils securityUtils) {
        this.amountsService = amountsService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/amounts.json")
    @Operation(summary = "Get transaction amounts for custom ranges")
    public ApiResponse<List<TransactionAmountsService.AmountsItem>> getAmounts(
            @RequestParam String query,
            @RequestParam(required = false) String exclude_account_ids,
            @RequestParam(required = false) String exclude_category_ids,
            @RequestParam(required = false) Boolean use_transaction_timezone) {

        List<Long> exclAccts = exclude_account_ids != null
                ? parseIdList(exclude_account_ids) : null;
        List<Long> exclCats = exclude_category_ids != null
                ? parseIdList(exclude_category_ids) : null;

        return ApiResponse.success(amountsService.getAmounts(query, exclAccts, exclCats));
    }

    private List<Long> parseIdList(String s) {
        return java.util.Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .map(Long::parseLong)
                .toList();
    }
}