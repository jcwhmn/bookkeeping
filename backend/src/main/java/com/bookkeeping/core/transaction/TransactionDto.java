package com.bookkeeping.core.transaction;

import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = Transaction.class, direction = Direction.From)
public record TransactionDto(
    Long id,
    Integer transactionType,
    Long accountId,
    Long categoryId,
    Long amount,
    String description,
    Long transactionTime,
    Long relatedId,
    Long userId,
    String tagIds
) {}