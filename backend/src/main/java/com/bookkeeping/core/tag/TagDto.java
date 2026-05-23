package com.bookkeeping.core.tag;

import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = Tag.class, direction = Direction.From)
public record TagDto(
    Long id,
    String name,
    String color,
    Long createdTime
) {}