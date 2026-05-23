package com.bookkeeping.core.tag;

public record UpdateTagRequest(
    String name,
    String color
) {}