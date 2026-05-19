package com.bookkeeping.supporting.auth;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.JwtTokenProvider;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new BusinessException(201001, "Invalid username or password"));
        
        // Check if user is disabled
        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new BusinessException(201001, "Invalid username or password");
        }
        
        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(201001, "Invalid username or password");
        }
        
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());
        
        long expiresAt = Instant.now().getEpochSecond() + tokenProvider.getAccessTokenExpiry();
        String defaultCurrency = user.getDefaultCurrency() != null ? user.getDefaultCurrency() : "USD";
        
        return new LoginResponse(
            accessToken,
            refreshToken,
            String.valueOf(expiresAt),
            new LoginResponse.UserInfo(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getNickname() != null ? user.getNickname() : user.getUsername(),
                defaultCurrency
            )
        );
    }
}