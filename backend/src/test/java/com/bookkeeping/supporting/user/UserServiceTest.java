package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                
                .username("testuser")
                .email("test@example.com")
                .nickname("Test User")
                .password("hashedpassword")
                .salt("salt123")
                .defaultCurrency("USD")
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build().withId(1L);
    }

    @Test
    void findById_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_nonExistingUser_returnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(99L);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(99L);
    }

    @Test
    void getById_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsername_existingUser_returnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findByUsername_nonExistingUser_returnsEmpty() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUsername_existingUsername_returnsTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_nonExistingUsername_returnsFalse() {
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        assertFalse(userService.existsByUsername("nonexistent"));
    }

    @Test
    void save_user_savesAndReturns() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.save(testUser);

        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_withValidRequest_callsMapper() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(
                new UserDto(1L, "testuser", "test@example.com", "New Nick", "USD", null, "en-US", null, 1, 1, "YYYY-MM-DD", 1));

        UpdateUserRequest request = new UpdateUserRequest("New Nick", null, null, null, null, null, null, null, null);
        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        assertEquals("New Nick", result.nickname());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_withAvatar_updatesAvatar() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(
                new UserDto(1L, "testuser", "test@example.com", "Test User", "USD", null, "en-US",
                        "/avatars/new.jpg", 1, 1, "YYYY-MM-DD", 1));

        UpdateUserRequest request = new UpdateUserRequest(null, null, null, null, "/avatars/new.jpg", null, null, null, null);
        UserDto result = userService.updateProfile(1L, request);

        assertNotNull(result);
        assertEquals("/avatars/new.jpg", result.avatar());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void isActive_withVerifiedEnabledUser_returnsTrue() {
        User user = testUser.toBuilder().emailVerified(true).disabled(false).build().withId(1L);
        assertTrue(user.isActive());
    }

    @Test
    void isActive_withDisabledUser_returnsFalse() {
        User user = testUser.toBuilder().emailVerified(true).disabled(true).build().withId(1L);
        assertFalse(user.isActive());
    }

    @Test
    void isActive_withUnverifiedUser_returnsTrue() {
        User user = testUser.toBuilder().emailVerified(false).disabled(false).build().withId(1L);
        assertTrue(user.isActive());
    }
}
