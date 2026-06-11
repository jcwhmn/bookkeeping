package com.bookkeeping.core.mcp;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.transaction.*;
import com.bookkeeping.core.account.AccountRepository;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.core.category.CategoryService;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP (Model Context Protocol) Server for AI Agent Integration
 * 
 * This controller provides a simplified JSON-RPC style interface for AI agents
 * to interact with the bookkeeping system. AI agents can use these endpoints to:
 * - Query transactions, accounts, categories
 * - Create, update, delete transactions
 * - Get statistics and insights
 * - Manage scheduled transactions
 */
@RestController
@RequestMapping("/api/v1/mcp")
@Tag(name = "MCP", description = "Model Context Protocol for AI agents")
public class McpController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    public McpController(
            TransactionService transactionService,
            AccountService accountService,
            CategoryService categoryService,
            SecurityUtils securityUtils) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.securityUtils = securityUtils;
    }

    // === Tool Definitions (for AI agent discovery) ===

    @GetMapping("/tools")
    @Operation(summary = "List available MCP tools")
    public Map<String, Object> listTools() {
        return Map.of(
            "tools", List.of(
                Map.of(
                    "name", "get_transactions",
                    "description", "Get transactions with optional filters",
                    "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "year", Map.of("type", "integer", "description", "Year"),
                            "month", Map.of("type", "integer", "description", "Month (1-12)"),
                            "accountId", Map.of("type", "integer", "description", "Account ID"),
                            "limit", Map.of("type", "integer", "description", "Max results (default 100)")
                        )
                    )
                ),
                Map.of(
                    "name", "create_transaction",
                    "description", "Create a new transaction",
                    "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "transactionType", Map.of("type", "integer", "enum", List.of(2, 3, 4), "description", "2=income, 3=expense, 4=transfer"),
                            "accountId", Map.of("type", "integer", "description", "Source account ID"),
                            "destinationAccountId", Map.of("type", "integer", "description", "For transfers only"),
                            "amount", Map.of("type", "integer", "description", "Amount in cents"),
                            "description", Map.of("type", "string", "description", "Transaction description"),
                            "categoryId", Map.of("type", "integer", "description", "Category ID"),
                            "transactionTime", Map.of("type", "integer", "description", "Unix timestamp")
                        ),
                        "required", List.of("transactionType", "accountId", "amount")
                    )
                ),
                Map.of(
                    "name", "get_accounts",
                    "description", "Get all accounts with balances"
                ),
                Map.of(
                    "name", "get_statistics",
                    "description", "Get income/expense statistics for a month",
                    "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "year", Map.of("type", "integer", "description", "Year"),
                            "month", Map.of("type", "integer", "description", "Month (1-12)")
                        )
                    )
                ),
                Map.of(
                    "name", "search_transactions",
                    "description", "Search transactions by text",
                    "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "query", Map.of("type", "string", "description", "Search text"),
                            "limit", Map.of("type", "integer", "description", "Max results")
                        ),
                        "required", List.of("query")
                    )
                )
            )
        );
    }

    // === Tool Implementations ===

    @PostMapping("/call")
    @Operation(summary = "Execute MCP tool")
    public McpResponse callTool(@RequestBody McpRequest request) {
        try {
            Object result = switch (request.tool()) {
                case "get_transactions" -> getTransactions(request.params());
                case "create_transaction" -> createTransaction(request.params());
                case "get_accounts" -> getAccounts();
                case "get_categories" -> getCategories();
                case "get_statistics" -> getStatistics(request.params());
                case "search_transactions" -> searchTransactions(request.params());
                case "update_transaction" -> updateTransaction(request.params());
                case "delete_transaction" -> deleteTransaction(request.params());
                default -> throw new BusinessException(ResultCode.NOT_FOUND, "Unknown tool: " + request.tool());
            };
            return new McpResponse(true, result, null);
        } catch (Exception e) {
            return new McpResponse(false, null, e.getMessage());
        }
    }

    // === Tool Handlers ===

    private List<TransactionDto> getTransactions(Map<String, Object> params) {
        Integer year = params.get("year") != null ? ((Number) params.get("year")).intValue() : null;
        Integer month = params.get("month") != null ? ((Number) params.get("month")).intValue() : null;
        int limit = params.get("limit") != null ? ((Number) params.get("limit")).intValue() : 100;

        if (year != null && month != null) {
            return transactionService.getTransactionsByMonth(year, month);
        }
        // Default: current month
        java.time.LocalDate now = java.time.LocalDate.now();
        return transactionService.getTransactionsByMonth(now.getYear(), now.getMonthValue());
    }

    private TransactionDto createTransaction(Map<String, Object> params) {
        CreateTransactionRequest request = new CreateTransactionRequest(
                ((Number) params.get("transactionType")).intValue(),
                ((Number) params.get("accountId")).longValue(),
                params.get("categoryId") != null ? ((Number) params.get("categoryId")).longValue() : null,
                params.get("destinationAccountId") != null ? ((Number) params.get("destinationAccountId")).longValue() : null,
                ((Number) params.get("amount")).longValue(),
                (String) params.getOrDefault("description", ""),
                params.get("transactionTime") != null ? ((Number) params.get("transactionTime")).longValue() : null,
                (String) params.get("tagIds")
        );
        return transactionService.createTransaction(request);
    }

    private List<Map<String, Object>> getAccounts() {
        return accountService.getCurrentUserAccounts().stream().map(dto -> Map.<String, Object>of(
                "id", dto.id(),
                "name", dto.name(),
                "type", dto.accountType().name(),
                "balance", dto.balance(),
                "currency", dto.currency()
        )).toList();
    }

    private List<Map<String, Object>> getCategories() {
        return categoryService.getCurrentUserCategories().stream().map(dto -> Map.<String, Object>of(
                "id", dto.id(),
                "name", dto.name(),
                "type", dto.categoryType()
        )).toList();
    }

    private StatisticsDto getStatistics(Map<String, Object> params) {
        Integer year = params.get("year") != null ? ((Number) params.get("year")).intValue() : null;
        Integer month = params.get("month") != null ? ((Number) params.get("month")).intValue() : null;

        java.time.LocalDate now = java.time.LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();

        return transactionService.getStatistics(targetYear, targetMonth);
    }

    private List<TransactionDto> searchTransactions(Map<String, Object> params) {
        String query = (String) params.get("query");
        int limit = params.get("limit") != null ? ((Number) params.get("limit")).intValue() : 50;
        
        TransactionSearchParams searchParams = new TransactionSearchParams(
            null, null, null, null, null, query,
            null, null, null, null, null
        );
        return transactionService.searchTransactions(searchParams, limit);
    }

    private TransactionDto updateTransaction(Map<String, Object> params) {
        Long id = ((Number) params.get("id")).longValue();
        UpdateTransactionRequest request = new UpdateTransactionRequest(
                ((Number) params.get("transactionType")).intValue(),
                ((Number) params.get("accountId")).longValue(),
                params.get("categoryId") != null ? ((Number) params.get("categoryId")).longValue() : null,
                params.get("destinationAccountId") != null ? ((Number) params.get("destinationAccountId")).longValue() : null,
                ((Number) params.get("amount")).longValue(),
                (String) params.get("description"),
                params.get("transactionTime") != null ? ((Number) params.get("transactionTime")).longValue() : null,
                (String) params.get("tagIds")
        );
        return transactionService.updateTransaction(id, request);
    }

    private Map<String, Object> deleteTransaction(Map<String, Object> params) {
        Long id = ((Number) params.get("id")).longValue();
        transactionService.deleteTransaction(id);
        return Map.of("success", true, "deleted", id);
    }

    // === Request/Response Records ===

    public record McpRequest(String tool, Map<String, Object> params) {}
    public record McpResponse(boolean success, Object result, String error) {}
}