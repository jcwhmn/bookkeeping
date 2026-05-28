package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.core.account.AccountService;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TransactionImportService {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    // Parsed import session (in-memory for simplicity; could use Redis/DB for production)
    private final Map<String, ImportSession> sessions = new HashMap<>();

    public TransactionImportService(TransactionService transactionService,
                                     AccountService accountService,
                                     SecurityUtils securityUtils) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.securityUtils = securityUtils;
    }

    // === Parse Custom Format (CSV/TSV) ===

    /**
     * Parse CSV/TSV with custom column mapping.
     */
    public ParseResult parseCustom(String rawData, ParseRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        String delimiter = request.delimiter() != null ? request.delimiter() : ",";

        List<String[]> rows = parseLines(rawData != null ? rawData : request.rawData(), delimiter);
        if (rows.isEmpty()) {
            return new ParseResult(List.of(), "No data found", 0);
        }

        String[] headers = rows.get(0);
        List<ParsedTransaction> parsed = new ArrayList<>();
        int skipped = 0;

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length < 2) { skipped++; continue; }

            ParsedTransaction pt = mapRowToTransaction(row, headers, request, i + 1);
            if (pt != null) {
                parsed.add(pt);
            } else {
                skipped++;
            }
        }

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ImportSession(sessionId, userId, parsed, request.accountId()));

        return new ParseResult(
                parsed.stream().limit(100).toList(),
                skipped > 0 ? skipped + " rows skipped" : "OK",
                parsed.size()
        );
    }

    private ParsedTransaction mapRowToTransaction(String[] row, String[] headers,
                                                   ParseRequest request, int rowNum) {
        try {
            Long accountId = request.accountId();
            Integer dateCol = request.dateColumn();
            Integer amountCol = request.amountColumn();
            Integer typeCol = request.typeColumn();
            Integer descCol = request.descriptionColumn();
            Integer categoryCol = request.categoryColumn();
            Integer tagCol = request.tagColumn();

            // Get values by column index
            String dateStr = dateCol != null && dateCol < row.length ? row[dateCol].trim() : null;
            String amountStr = amountCol != null && amountCol < row.length ? row[amountCol].trim() : null;
            String typeStr = typeCol != null && typeCol < row.length ? row[typeCol].trim() : null;
            String desc = descCol != null && descCol < row.length ? row[descCol].trim() : null;
            String category = categoryCol != null && categoryCol < row.length ? row[categoryCol].trim() : null;
            String tag = tagCol != null && tagCol < row.length ? row[tagCol].trim() : null;

            if (dateStr == null || dateStr.isEmpty() || amountStr == null || amountStr.isEmpty()) {
                return null;
            }

            Long transactionTime = parseDate(dateStr, request.dateFormat());
            Long amount = parseAmount(amountStr);
            Integer transactionType = parseType(typeStr, amount);

            return new ParsedTransaction(
                    accountId, transactionTime, transactionType, amount, desc,
                    category, tag, rowNum, null
            );
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseDate(String dateStr, String format) {
        if (format != null && !format.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            } catch (DateTimeParseException ignored) {}
        }
        // Try common formats
        String[] formats = {"yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd", "yyyyMMdd"};
        for (String f : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(f);
                LocalDate date = LocalDate.parse(dateStr, formatter);
                return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            } catch (DateTimeParseException ignored) {}
        }
        throw new BusinessException(ResultCode.VALIDATION_ERROR, "Invalid date format: " + dateStr);
    }

    private Long parseAmount(String amountStr) {
        // Remove currency symbols, commas, spaces
        String cleaned = amountStr.replaceAll("[^\\d.\\-]", "");
        if (cleaned.isEmpty()) return 0L;
        double value = Double.parseDouble(cleaned);
        return (long) (value * 100); // Store as cents
    }

    private Integer parseType(String typeStr, Long amount) {
        if (typeStr == null || typeStr.isEmpty()) {
            return amount >= 0 ? 2 : 3; // Income if positive, expense if negative
        }
        String lower = typeStr.toLowerCase();
        return switch (lower) {
            case "income", "credit", "+" -> 2;
            case "expense", "debit", "-" -> 3;
            default -> amount >= 0 ? 2 : 3;
        };
    }

    private List<String[]> parseLines(String rawData, String delimiter) {
        List<String[]> lines = new ArrayList<>();
        String[] rows = rawData.split("\n");
        for (String row : rows) {
            row = row.trim();
            if (row.isEmpty()) continue;
            lines.add(row.split(delimiter, -1));
        }
        return lines;
    }

    // === Execute Import ===

    /**
     * Execute import from a parsed session.
     */
    @Transactional
    public ImportResult executeImport(String sessionId, Map<Integer, Long> rowCategoryMap) {
        Long userId = securityUtils.requireCurrentUser().getId();
        ImportSession session = sessions.get(sessionId);
        if (session == null || !session.userId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Import session not found");
        }

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        for (ParsedTransaction pt : session.parsed()) {
            try {
                CreateTransactionRequest req = new CreateTransactionRequest(
                        pt.transactionType(),
                        pt.accountId(),
                        rowCategoryMap.getOrDefault(pt.rowNumber(), null),
                        null, // destinationAccountId
                        pt.amount(),
                        pt.description() != null ? pt.description() : "Imported",
                        pt.transactionTime(),
                        pt.tag()
                );
                transactionService.createTransaction(req);
                imported++;
            } catch (Exception e) {
                errors.add("Row " + pt.rowNumber() + ": " + e.getMessage());
                skipped++;
            }
        }

        sessions.remove(sessionId); // Clean up
        return new ImportResult(imported, skipped, errors);
    }

    /**
     * Check import process status (for async imports - currently sync, always complete).
     */
    public ImportProcessResult checkProcess(String sessionId) {
        ImportSession session = sessions.get(sessionId);
        if (session == null) {
            return new ImportProcessResult("complete", 0, 0, List.of());
        }
        return new ImportProcessResult("complete", session.parsed().size(), 0, List.of());
    }

    // === DTOs ===

    public record ParseRequest(
            String rawData,
            Long accountId,
            String delimiter,
            Integer dateColumn,
            Integer amountColumn,
            Integer typeColumn,
            Integer descriptionColumn,
            Integer categoryColumn,
            Integer tagColumn,
            String dateFormat
    ) {}

    public record ParseResult(List<ParsedTransaction> preview, String message, int totalRows) {}

    public record ParsedTransaction(
            Long accountId,
            Long transactionTime,
            Integer transactionType,
            Long amount,
            String description,
            String category,
            String tag,
            int rowNumber,
            Long categoryId
    ) {}

    public record ImportResult(int imported, int skipped, List<String> errors) {}

    public record ImportProcessResult(String status, int total, int processed, List<String> errors) {}

    private record ImportSession(String sessionId, Long userId, List<ParsedTransaction> parsed, Long accountId) {}
}