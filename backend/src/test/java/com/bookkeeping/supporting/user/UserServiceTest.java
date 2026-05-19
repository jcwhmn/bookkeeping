package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setDefaultCurrency("USD");
        testUser.setLanguage("en-US");
        testUser.setDisabled(false);
    }

    @Nested
    @DisplayName("getCurrentUserDto()")
    class GetCurrentUserDtoTests {

        @Test
        @DisplayName("✓ Success: get current user DTO")
        void getCurrentUserDto_withValidId_returnsUserDto() {
            when(userRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(testUser));

            UserDto result = userService.getCurrentUserDto(1L);

            assertThat(result.idStr()).isEqualTo("1");
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.nickname()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("✗ Failure: user not found")
        void getCurrentUserDto_userNotFound_throwsException() {
            when(userRepository.findByIdNotDeleted(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUserDto(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        @Test
        @DisplayName("✓ Success: update user fields")
        void updateUser_withValidData_returnsUpdatedUserDto() {
            when(userRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
                    "New Nickname",
                    "EUR",
                    "zh-CN"
            );

            UserDto result = userService.updateUser(1L, request);

            assertThat(result.nickname()).isEqualTo("New Nickname");
        }

        @Test
        @DisplayName("✓ Success: update partial fields")
        void updateUser_withPartialData_returnsUpdatedUserDto() {
            when(userRepository.findByIdNotDeleted(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
                    "Only Nickname",
                    null,
                    null
            );

            UserDto result = userService.updateUser(1L, request);

            assertThat(result.nickname()).isEqualTo("Only Nickname");
            assertThat(result.defaultCurrency()).isEqualTo("USD"); // unchanged
        }

        @Test
        @DisplayName("✗ Failure: update non-existent user")
        void updateUser_userNotFound_throwsException() {
            when(userRepository.findByIdNotDeleted(999L)).thenReturn(Optional.empty());

            UserController.UpdateUserRequest request = new UserController.UpdateUserRequest("Name", "EUR", null);

            assertThatThrownBy(() -> userService.updateUser(999L, request))
                    .isInstanceOf(BusinessException.class);
        }
    }
}