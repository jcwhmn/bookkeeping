package com.bookkeeping.core.llm;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/llm")
@Tag(name = "LLM", description = "AI/LLM integration APIs")
public class LlmController {

    private final LlmService llmService;
    private final SecurityUtils securityUtils;

    public LlmController(LlmService llmService, SecurityUtils securityUtils) {
        this.llmService = llmService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/transactions/recognize_receipt_image.json")
    @Operation(summary = "Recognize transaction from receipt image using AI")
    public ApiResponse<LlmService.RecognizeResult> recognizeReceipt(
            @RequestParam("picture") MultipartFile file,
            @RequestParam(value = "account_id", required = true) Long accountId) throws IOException {

        byte[] imageBytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        LlmService.RecognizeResult result = llmService.recognizeReceiptImage(base64, accountId);
        return ApiResponse.success(result);
    }
}