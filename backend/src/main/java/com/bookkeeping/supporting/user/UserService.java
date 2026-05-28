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

        // Update fields directly on the attached entity
        if (request.nickname() != null) user.setNickname(request.nickname());
        if (request.defaultCurrency() != null) user.setDefaultCurrency(request.defaultCurrency());
        if (request.language() != null) user.setLanguage(request.language());
        if (request.defaultAccountId() != null) user.setDefaultAccountId(request.defaultAccountId());
        if (request.avatar() != null) user.setAvatar(request.avatar());
        if (request.transactionEditScope() != null) user.setTransactionEditScope(request.transactionEditScope());
        if (request.firstDayOfWeek() != null) user.setFirstDayOfWeek(request.firstDayOfWeek());
        if (request.fiscalYearStart() != null) user.setFiscalYearStart(request.fiscalYearStart());
        if (request.dateFormat() != null) user.setDateFormat(request.dateFormat());
        
        return userMapper.toDto(userRepository.save(user));
    }
}