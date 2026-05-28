package com.bookkeeping.core.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction picture")
public record TransactionPictureDto(
        Long id,
        Long transactionId,
        String fileName,
        Long fileSize,
        String mimeType,
        Long createdAt
) {}