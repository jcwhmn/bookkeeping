package com.bookkeeping.supporting.auth;

import com.bookkeeping.config.security.JwtTokenProvider;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserDto;
import com.bookkeeping.supporting.user.UserMapper;
import com.bookkeeping.supporting.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       UserMapper userMapper,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(
                        ResultCode.AUTHENTICATION_FAILED,
                        "Invalid username or password"));

        String hashedPassword = hashPassword(request.password(), user.getSalt());
        if (!hashedPassword.equals(user.getPassword())) {
            throw new BusinessException(
                    ResultCode.AUTHENTICATION_FAILED,
                    "Invalid username or password");
        }

        if (!user.isActive()) {
            log.warn("Login failed: user {} is disabled", user.getUsername());
            throw new BusinessException(
                    ResultCode.USER_DISABLED,
                    "User account is disabled");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        log.info("User {} logged in successfully", user.getUsername());
        return new LoginResponse(token, userMapper.toDto(user));
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(
                    ResultCode.USERNAME_ALREADY_EXISTS,
                    "Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    ResultCode.EMAIL_ALREADY_EXISTS,
                    "Email already exists");
        }

        String salt = generateSalt();
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(hashPassword(request.password(), salt))
                .salt(salt)
                .nickname(request.username())
                .defaultCurrency("USD")
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully (id={})", savedUser.getUsername(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    private String hashPassword(String password, String salt) {
        try {
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    private String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new BusinessException(
                        ResultCode.USER_NOT_FOUND,
                        "User not found"));
    }
}