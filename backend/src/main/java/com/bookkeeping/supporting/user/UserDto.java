package com.bookkeeping.supporting.user;

import com.jcwhmn.annotations.MapperAuto;
import com.jcwhmn.annotations.Direction;

/**
 * User response DTO.
 */
@MapperAuto(sourceEntity = User.class, direction = Direction.From)
public record UserDto(
    Long id,
    String username,
    String email,
    String nickname,
    String defaultCurrency,
    Long defaultAccountId,
    String language
) {
    // Use UserDtoMapperConverter.toDto(user) for mapping from User entity
}