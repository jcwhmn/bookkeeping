package com.bookkeeping.supporting.auth;

import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.JwtTokenProvider;
import com.bookkeeping.supporting.user.User;
import com.bookkeeping.supporting.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("Test User");
        testUser.setPassword("hashedPassword");
        testUser.setDefaultCurrency("USD");
        testUser.setDisabled(false);
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("✓ Success: login with valid credentials")
        void login_withValidCredentials_returnsLoginResponse() {
            // Given
            LoginRequest request = new LoginRequest("testuser", "password123");
            when(userRepository.findByUsernameNotDeleted("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
            when(tokenProvider.generateAccessToken(1L, "testuser")).thenReturn("access-token");
            when(tokenProvider.generateRefreshToken(1L)).thenReturn("refresh-token");
            when(tokenProvider.getAccessTokenExpiry()).thenReturn(3600L);

            // When
            LoginResponse response = authService.login(request);

            // Then
            assertThat(response.token()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.user().username()).isEqualTo("testuser");
            assertThat(response.user().nickname()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("✗ Failure: user not found")
        void login_userNotFound_throwsException() {
            // Given
            LoginRequest request = new LoginRequest("nonexistent", "password123");
            when(userRepository.findByUsernameNotDeleted("nonexistent")).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(201001);
                    });
        }

        @Test
        @DisplayName("✗ Failure: wrong password")
        void login_wrongPassword_throwsException() {
            // Given
            LoginRequest request = new LoginRequest("testuser", "wrongpassword");
            when(userRepository.findByUsernameNotDeleted("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(201001);
                    });
        }

        @Test
        @DisplayName("✗ Failure: disabled user")
        void login_disabledUser_throwsException() {
            // Given
            testUser.setDisabled(true);
            LoginRequest request = new LoginRequest("testuser", "password123");
            when(userRepository.findByUsernameNotDeleted("testuser")).thenReturn(Optional.of(testUser));

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getCode()).isEqualTo(201001);
                    });
        }
    }
}