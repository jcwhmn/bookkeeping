package com.bookkeeping.supporting.user;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public UserDto getCurrentUserDto(Long userId) {
        User user = userRepository.findByIdNotDeleted(userId)
            .orElseThrow(() -> BusinessException.notFound(ResultCode.USER_NOT_FOUND));
        return UserDto.fromEntity(user);
    }
    
    @Transactional
    public UserDto updateUser(Long userId, UserController.UpdateUserRequest request) {
        User user = userRepository.findByIdNotDeleted(userId)
            .orElseThrow(() -> BusinessException.notFound(ResultCode.USER_NOT_FOUND));
        
        if (request.nickname() != null) {
            user.setNickname(request.nickname());
        }
        if (request.defaultCurrency() != null) {
            user.setDefaultCurrency(request.defaultCurrency());
        }
        if (request.language() != null) {
            user.setLanguage(request.language());
        }
        
        User saved = userRepository.save(user);
        log.info("User updated: {}", userId);
        
        return UserDto.fromEntity(saved);
    }
}