package com.bookkeeping.supporting.auth;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserDto;
import com.bookkeeping.supporting.user.UserMapper;
import com.bookkeeping.supporting.user.UserRepository;
import com.bookkeeping.supporting.auth.AuthService;
import com.bookkeeping.supporting.auth.LoginRequest;
import com.bookkeeping.supporting.auth.RegisterRequest;
import com.bookkeeping.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceIntegrationTest {

    private AuthService authService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private JwtTokenProvider jwtProvider;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        jwtProvider = mock(JwtTokenProvider.class);
        when(jwtProvider.generateToken(anyString())).thenReturn("mock_token_123");

        authService = new AuthService(userRepository, userMapper, jwtProvider);
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-001: Register creates user with emailVerified=true, disabled=false")
    void register_createsUserWithCorrectDefaults() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.toBuilder().build(); // simulate JPA id generation
        });
        when(userMapper.toDto(any(User.class))).thenReturn(
            new UserDto(1L, "newuser", "new@test.com", "newuser", "USD", null, "en-US", null, 1, 1, "YYYY-MM-DD", 1));

        RegisterRequest req = new RegisterRequest("newuser", "new@test.com", "pass123");
        UserDto result = authService.register(req);

        assertNotNull(result);
        verify(userRepository).save(argThat(u ->
            u.getUsername().equals("newuser") &&
            u.getEmail().equals("new@test.com") &&
            u.getEmailVerified() == Boolean.TRUE &&
            u.getDisabled() == Boolean.FALSE
        ));
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-002: Login returns token and user info for valid credentials")
    void login_returnsTokenForValidCredentials() {
        User user = createUser("demo", "demo123");
        when(userRepository.findByUsername("demo")).thenReturn(java.util.Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(
            new UserDto(1L, "demo", "demo@example.com", "Demo", "USD", null, "en-US", null, 1, 1, "YYYY-MM-DD", 1));

        LoginRequest req = new LoginRequest("demo", "demo123");
        var result = authService.login(req);

        assertNotNull(result.token());
        assertEquals("demo", result.user().username());
        verify(jwtProvider).generateToken("demo");
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-003: Login throws AUTHENTICATION_FAILED for wrong password")
    void login_throwsForWrongPassword() {
        User user = createUser("demo", "demo123");
        when(userRepository.findByUsername("demo")).thenReturn(java.util.Optional.of(user));

        LoginRequest req = new LoginRequest("demo", "wrongpassword");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
        assertEquals(ResultCode.AUTHENTICATION_FAILED.getCode(), ex.getErrorCode());
        assertTrue(ex.getErrorMessage().toLowerCase().contains("invalid") || ex.getErrorMessage().contains("password"));
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-004: Login throws USER_DISABLED for disabled user")
    void login_throwsForDisabledUser() {
        User disabled = createUser("disabled", "pass").toBuilder().disabled(true).build();
        when(userRepository.findByUsername("disabled")).thenReturn(java.util.Optional.of(disabled));

        LoginRequest req = new LoginRequest("disabled", "pass");
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
        assertEquals(ResultCode.USER_DISABLED.getCode(), ex.getErrorCode());
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-005: Register throws USERNAME_ALREADY_EXISTS for duplicate")
    void register_failsForDuplicateUsername() {
        when(userRepository.existsByUsername("demo")).thenReturn(true);

        RegisterRequest req = new RegisterRequest("demo", "another@test.com", "pass");
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals(ResultCode.USERNAME_ALREADY_EXISTS.getCode(), ex.getErrorCode());
    }

    @Test
    @DisplayName("TC-AUTH-AUTO-006: Register throws EMAIL_ALREADY_EXISTS for duplicate email")
    void register_failsForDuplicateEmail() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("demo@example.com")).thenReturn(true);

        RegisterRequest req = new RegisterRequest("newuser", "demo@example.com", "pass");
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals(ResultCode.EMAIL_ALREADY_EXISTS.getCode(), ex.getErrorCode());
    }

    private User createUser(String username, String password) {
        return User.builder()
                
                .username(username)
                .email(username + "@example.com")
                .salt("salt123")
                .password(hash("salt123", password))
                .nickname(username)
                .emailVerified(true)
                .disabled(false)
                .build();
    }

    private String hash(String salt, String password) {
        try {
            var md = java.security.MessageDigest.getInstance("MD5");
            byte[] d = md.digest((salt + password).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
