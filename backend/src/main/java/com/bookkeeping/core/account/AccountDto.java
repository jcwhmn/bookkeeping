package com.bookkeeping.core.account;

import com.bookkeeping.common.enums.AccountType;
import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

/**
 * Account response DTO.
 * Balance is stored as Long (cents), frontend divides by 100.
 */
@MapperAuto(sourceEntity = Account.class, direction = Direction.From)
public record AccountDto(
    Long id,
    String name,
    AccountType accountType,
    String currency,
    Long balance,
    Long userId,
    String description,
    Long parentId,
    Integer sortOrder,
    Boolean hidden
) {}