package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for user management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Get full user profile (for /me endpoint).
     */
    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        return userMapper.toDto(getById(userId));
    }

    /**
     * Update user profile fields.
     */
    public UserDto updateProfile(Long userId, UpdateUserRequest request) {
        User user = getById(userId);

        User.UserBuilder builder = user.toBuilder();
        if (request.nickname() != null) builder.nickname(request.nickname());
        if (request.defaultCurrency() != null) builder.defaultCurrency(request.defaultCurrency());
        if (request.language() != null) builder.language(request.language());
        if (request.defaultAccountId() != null) builder.defaultAccountId(request.defaultAccountId());
        if (request.avatar() != null) builder.avatar(request.avatar());
        if (request.transactionEditScope() != null)
            builder.transactionEditScope(request.transactionEditScope());
        if (request.firstDayOfWeek() != null)
            builder.firstDayOfWeek(request.firstDayOfWeek());
        if (request.fiscalYearStart() != null)
            builder.fiscalYearStart(request.fiscalYearStart());
        if (request.dateFormat() != null)
            builder.dateFormat(request.dateFormat());
        return userMapper.toDto(save(builder.build()));
    }
}