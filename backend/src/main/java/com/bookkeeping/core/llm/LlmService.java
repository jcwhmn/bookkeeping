package com.bookkeeping.core.llm;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * LLM-powered receipt recognition service.
 * Parses receipt images to extract transaction data.
 *
 * Currently a stub - plug in your LLM provider (OpenAI, Anthropic, etc.)
 */
@Service
public class LlmService {

    @Value("${llm.provider:none}")
    private String provider;

    @Value("${llm.api-key:}")
    private String apiKey;

    @Value("${llm.endpoint:}")
    private String endpoint;

    private final SecurityUtils securityUtils;

    public LlmService(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    /**
     * Recognize transaction from receipt image.
     * Currently a stub that returns example data.
     */
    public RecognizeResult recognizeReceiptImage(String imageBase64, Long accountId) {
        Long userId = securityUtils.requireCurrentUser().getId();

        if ("openai".equalsIgnoreCase(provider) && !apiKey.isEmpty()) {
            return callOpenAI(imageBase64, accountId, userId);
        }

        // Stub: Return example recognition result
        return new RecognizeResult(
                2,                  // transactionType (income)
                accountId,
                250000L,            // amount (2500.00)
                "Grocery Store",
                12345L,             // categoryId (example)
                "Receipt from store",
                System.currentTimeMillis() / 1000,
                true,
                "Stub: Configure llm.provider and llm.api-key for real recognition"
        );
    }

    private RecognizeResult callOpenAI(String imageBase64, Long accountId, Long userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // This is a simplified example - implement full OpenAI Vision API call
            Map<String, Object> body = Map.of(
                    "model", "gpt-4o",
                    "messages", new Object[]{
                            Map.of("role", "user", "content", new Object[]{
                                    Map.of("type", "text", "text", "Extract transaction from receipt: amount, description, date"),
                                    Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + imageBase64))
                            })
                    }
            );
            // TODO: Make actual HTTP call and parse response
            throw new BusinessException(ResultCode.BAD_REQUEST, "LLM integration pending");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "LLM call failed: " + e.getMessage());
        }
    }

    /**
     * Configure LLM provider via properties:
     *
     * llm.provider=openai
     * llm.api-key=sk-...
     * llm.endpoint=https://api.openai.com/v1/chat/completions
     */
    public record RecognizeResult(
            Integer transactionType,
            Long accountId,
            Long amount,
            String description,
            Long categoryId,
            String transactionTime,
            Long unixTime,
            boolean success,
            String message
    ) {}
}