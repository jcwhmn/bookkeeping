package com.bookkeeping.core.llm;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/llm")
@Tag(name = "LLM", description = "AI/LLM integration APIs")
public class LlmController {

    @PostMapping("/transactions/recognize_receipt_image.json")
    @Operation(summary = "Recognize transaction from receipt image")
    public ApiResponse<Map<String, Object>> recognizeReceipt(
            @RequestParam("picture") MultipartFile file) {
        // Stub implementation — requires LLM provider configuration
        return ApiResponse.success(Map.of(
                "status", "not_configured",
                "message", "LLM provider not configured. Please set up your LLM API key in application settings."
        ));
    }
}