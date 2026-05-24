package com.bookkeeping.core.category;

import com.bookkeeping.common.enums.CategoryType;
import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = Category.class, direction = Direction.From)
public record CategoryDto(
    Long id,
    String name,
    CategoryType categoryType,
    Long userId,
    Long parentId,
    Integer sortOrder,
    Boolean hidden
) {}