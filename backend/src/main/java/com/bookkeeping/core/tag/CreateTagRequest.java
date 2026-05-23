package com.bookkeeping.core.tag;

public record CreateTagRequest(
    String name,
    String color
) {}