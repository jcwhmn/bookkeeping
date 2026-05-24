package com.bookkeeping.supporting.user;

import com.jcwhmn.annotations.Direction;
import com.jcwhmn.annotations.MapperAuto;

@MapperAuto(sourceEntity = User.class, direction = Direction.From)
public record UserDto(
    Long id,
    String username,
    String email,
    String nickname,
    String defaultCurrency,
    Long defaultAccountId,
    String language,
    String avatar,
    Integer firstDayOfWeek,
    Integer fiscalYearStart,
    String dateFormat,
    Integer transactionEditScope
) {}