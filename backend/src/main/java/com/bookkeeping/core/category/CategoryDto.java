package com.bookkeeping.core.category;

import com.bookkeeping.common.enums.CategoryType;
import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

/**
 * Category response DTO.
 * categoryType is returned as enum and serialized to JSON as "INCOME", "EXPENSE", "TRANSFER".
 * Frontend can use this directly or convert to integers.
 */
@MapperAuto(sourceEntity = Category.class, direction = Direction.From)
public record CategoryDto(
    Long id,
    String name,
    CategoryType categoryType,
    Long userId,
    Long parentId,
    Integer sortOrder,
    Boolean hidden,
    String icon,
    String color,
    String comment
) {}
