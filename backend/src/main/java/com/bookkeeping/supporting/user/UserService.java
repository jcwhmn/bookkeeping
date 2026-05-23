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
    
    /**
     * Find user by ID.
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Find user by ID, throw if not found.
     */
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }
    
    /**
     * Find user by username.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by username, throw if not found.
     */
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }
    
    /**
     * Check if username exists.
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Save user.
     */
    public User save(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Update user profile.
     */
    public UserDto updateProfile(Long userId, UpdateUserRequest request) {
        User user = getById(userId);
        
        User.UserBuilder builder = user.toBuilder();
        if (request.nickname() != null) {
            builder.nickname(request.nickname());
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (existsByEmail(request.email())) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "Email already in use");
            }
            builder.email(request.email());
        }
        if (request.defaultCurrency() != null) {
            builder.defaultCurrency(request.defaultCurrency());
        }
        if (request.language() != null) {
            builder.language(request.language());
        }
        
        return userMapper.toDto(save(builder.build()));
    }
}