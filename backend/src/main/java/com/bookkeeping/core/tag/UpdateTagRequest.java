package com.bookkeeping.core.tag;

public record UpdateTagRequest(
    long id,
    String name,
    String color,
    Long groupId
) {}