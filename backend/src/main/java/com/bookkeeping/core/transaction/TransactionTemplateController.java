package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transaction/templates")
@Tag(name = "Transaction Templates", description = "Transaction template management APIs")
public class TransactionTemplateController {

    private final TransactionTemplateService templateService;
    private final SecurityUtils securityUtils;

    public TransactionTemplateController(TransactionTemplateService templateService, SecurityUtils securityUtils) {
        this.templateService = templateService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/list.json")
    @Operation(summary = "List transaction templates")
    public ApiResponse<List<TransactionTemplateDto>> list(
            @RequestParam(required = false) Integer templateType) {
        return ApiResponse.success(templateService.listTemplates(templateType));
    }

    @GetMapping("/get.json")
    @Operation(summary = "Get a single template")
    public ApiResponse<TransactionTemplateDto> get(@RequestParam Long id) {
        return ApiResponse.success(templateService.getTemplate(id));
    }

    @PostMapping("/add.json")
    @Operation(summary = "Create a template")
    public ApiResponse<TransactionTemplateDto> create(@RequestBody TransactionTemplateService.TransactionTemplateCreateRequest request) {
        return ApiResponse.success(templateService.createTemplate(request));
    }

    @PostMapping("/modify.json")
    @Operation(summary = "Modify a template")
    public ApiResponse<TransactionTemplateDto> modify(@RequestBody TransactionTemplateService.TransactionTemplateModifyRequest request) {
        return ApiResponse.success(templateService.modifyTemplate(request.id(), request));
    }

    @PostMapping("/hide.json")
    @Operation(summary = "Hide/unhide a template")
    public ApiResponse<Void> hide(@RequestBody IdHiddenRequest request) {
        templateService.hideTemplate(request.id(), request.hidden());
        return ApiResponse.success(null);
    }

    @PostMapping("/move.json")
    @Operation(summary = "Reorder templates")
    public ApiResponse<Void> move(@RequestBody OrderedIdsRequest request) {
        templateService.reorderTemplates(request.orderedIds());
        return ApiResponse.success(null);
    }

    @PostMapping("/delete.json")
    @Operation(summary = "Delete a template")
    public ApiResponse<Void> delete(@RequestBody IdRequest request) {
        templateService.deleteTemplate(request.id());
        return ApiResponse.success(null);
    }

    @PostMapping("/upload_picture.json")
    @Operation(summary = "Upload transaction picture")
    public ApiResponse<Map<String, String>> uploadPicture(
            @RequestParam("picture") MultipartFile file) throws IOException {
        Long userId = securityUtils.requireCurrentUser().getId();
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "File must be less than 5MB");
        }
        String filename = userId + "_" + System.currentTimeMillis() + ".jpg";
        Path uploadDir = Paths.get("backend/src/main/resources/static/pictures");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, file.getBytes());
        return ApiResponse.success(Map.of("pictureUrl", "/pictures/" + filename));
    }

    public record IdRequest(long id) {}
    public record IdHiddenRequest(long id, boolean hidden) {}
    public record OrderedIdsRequest(List<Long> orderedIds) {}
}