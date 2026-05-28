package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction/pictures")
@Tag(name = "Transaction Pictures", description = "Transaction picture management")
public class TransactionPictureController {

    private final TransactionPictureService pictureService;
    private final SecurityUtils securityUtils;

    public TransactionPictureController(TransactionPictureService pictureService, SecurityUtils securityUtils) {
        this.pictureService = pictureService;
        this.securityUtils = securityUtils;
    }

    @PostMapping(value = "/upload.json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload picture to transaction")
    public ApiResponse<TransactionPictureDto> upload(
            @RequestParam("transaction_id") Long transactionId,
            @RequestParam("file") MultipartFile file) throws IOException {
        byte[] data = file.getBytes();
        String mimeType = file.getContentType();
        TransactionPictureDto result = pictureService.uploadPicture(transactionId, file.getOriginalFilename(), data, mimeType);
        return ApiResponse.success(result);
    }

    @GetMapping("/list.json")
    @Operation(summary = "List pictures for a transaction")
    public ApiResponse<List<TransactionPictureDto>> list(@RequestParam("transaction_id") Long transactionId) {
        return ApiResponse.success(pictureService.listByTransaction(transactionId));
    }

    @GetMapping("/{pictureId}/file")
    @Operation(summary = "Get picture file")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable Long pictureId) {
        return pictureService.getFile(pictureId)
                .map(file -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(file.getMimeType() != null ? file.getMimeType() : "image/jpeg"))
                        .body(new FileSystemResource(file.getFilePath())))
                .orElse(ResponseEntity.notFound().<FileSystemResource>build());
    }

    @PostMapping("/remove.json")
    @Operation(summary = "Delete a picture")
    public ApiResponse<Void> remove(@RequestParam("picture_id") Long pictureId) {
        pictureService.deletePicture(pictureId);
        return ApiResponse.success(null);
    }

    @PostMapping("/remove_unused.json")
    @Operation(summary = "Remove unused pictures (no transaction reference)")
    public ApiResponse<PictureCleanupResult> removeUnused() {
        long count = pictureService.cleanupUnused();
        return ApiResponse.success(new PictureCleanupResult(count));
    }

    public record PictureCleanupResult(long removedCount) {}
}