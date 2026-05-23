package com.bookkeeping.supporting.auth;

import com.bookkeeping.config.security.JwtTokenProvider;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserDto;
import com.bookkeeping.supporting.user.UserMapper;
import com.bookkeeping.supporting.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    private User baseTestUser;
    private UserDto testUserDto;
    private String testSalt = "salt123";

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, userMapper, jwtTokenProvider);

        baseTestUser = User.builder()
                
                .username("testuser")
                .email("test@example.com")
                .nickname("Test User")
                .salt(testSalt)
                .emailVerified(true)
                .disabled(false)
                .build().withId(1L);

        testUserDto = new UserDto(1L, "testuser", "test@example.com", "Test User", "USD", null, "en-US");
    }

    private String hashPassword(String password, String salt) {
        try {
            String saltedPassword = salt + password;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(saltedPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void login_withValidCredentials_returnsLoginResponse() {
        String rawPassword = "password123";
        User testUser = baseTestUser.toBuilder().password(hashPassword(rawPassword, testSalt)).build().withId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt.token.here");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        LoginRequest request = new LoginRequest("testuser", rawPassword);
        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.token());
        assertEquals(testUserDto, response.user());
    }

    @Test
    void login_withInvalidUsername_throwsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown", "password123");
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(ResultCode.AUTHENTICATION_FAILED.getCode(), exception.getErrorCode());
    }

    @Test
    void login_withInvalidPassword_throwsException() {
        User testUser = baseTestUser.toBuilder().password(hashPassword("correctPassword", testSalt)).build().withId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        LoginRequest request = new LoginRequest("testuser", "wrongPassword");
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(ResultCode.AUTHENTICATION_FAILED.getCode(), exception.getErrorCode());
    }

    @Test
    void login_withDisabledUser_throwsException() {
        User testUser = baseTestUser.toBuilder()
                .disabled(true)
                .password(hashPassword("password123", testSalt))
                .build().withId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        LoginRequest request = new LoginRequest("testuser", "password123");
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));

        assertEquals(ResultCode.USER_DISABLED.getCode(), exception.getErrorCode());
    }

    @Test
    void login_withUnverifiedUser_canLogin() {
        User testUser = baseTestUser.toBuilder()
                .emailVerified(false)
                .password(hashPassword("password123", testSalt))
                .build().withId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt.token.here");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        LoginRequest request = new LoginRequest("testuser", "password123");
        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.token());
    }

    @Test
    void register_withNewUser_createsUserAndReturnsDto() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return u.toBuilder().build(); // simulate JPA id generation
        });
        when(userMapper.toDto(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserDto(u.getId(), u.getUsername(), u.getEmail(),
                    u.getNickname(), u.getDefaultCurrency(), u.getDefaultAccountId(), u.getLanguage());
        });

        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123");
        UserDto result = authService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.username());
        assertEquals("new@example.com", result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withExistingUsername_throwsException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("existinguser", "email@example.com", "password123");
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals(ResultCode.USERNAME_ALREADY_EXISTS.getCode(), exception.getErrorCode());
    }

    @Test
    void register_withExistingEmail_throwsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("newuser", "existing@example.com", "password123");
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals(ResultCode.EMAIL_ALREADY_EXISTS.getCode(), exception.getErrorCode());
    }

    @Test
    void register_setsCorrectDefaultValues() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            assertEquals("USD", u.getDefaultCurrency());
            assertEquals("en-US", u.getLanguage());
            assertTrue(u.getEmailVerified());
            assertFalse(u.getDisabled());
            assertNotNull(u.getSalt());
            assertNotNull(u.getPassword());
            return u.toBuilder().build().withId(1L);
        });
        when(userMapper.toDto(any())).thenReturn(testUserDto);

        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123");
        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_callsJwtProvider() {
        String rawPassword = "password123";
        User testUser = baseTestUser.toBuilder().password(hashPassword(rawPassword, testSalt)).build().withId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt.token.here");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        LoginRequest request = new LoginRequest("testuser", rawPassword);
        authService.login(request);

        verify(jwtTokenProvider).generateToken("testuser");
    }
}
