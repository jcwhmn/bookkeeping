package com.bookkeeping.core.tag;

import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = TagGroup.class, direction = Direction.From)
public record TagGroupDto(
    Long id,
    String name,
    String color,
    Long createdTime,
    Integer sortOrder
) {}