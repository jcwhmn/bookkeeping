package com.bookkeeping.core.transaction;

import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = TransactionTemplate.class, direction = Direction.From)
public record TransactionTemplateDto(
    Long id,
    Integer templateType,
    String name,
    Integer transactionType,
    Long categoryId,
    Long sourceAccountId,
    Long destinationAccountId,
    Long sourceAmount,
    Long destinationAmount,
    Boolean hideAmount,
    String description,
    String tagIds,
    Integer displayOrder,
    Boolean hidden,
    Long createdAt
) {}