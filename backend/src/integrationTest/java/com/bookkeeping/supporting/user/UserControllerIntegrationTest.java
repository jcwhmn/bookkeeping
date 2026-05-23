package com.bookkeeping.supporting.user;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserService and UserRepository.
 * Tests against real PostgreSQL database.
 */
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setPassword("hashedpassword");
        testUser.setSalt("salt123");
        testUser.setDefaultCurrency("USD");
        testUser.setLanguage("en-US");
        testUser.setEmailVerified(true);
        testUser.setDisabled(false);
        testUser = userRepository.save(testUser);
    }

    @Test
    void findById_existingUser_returnsUser() {
        var user = userService.findById(testUser.getId());
        
        assertTrue(user.isPresent());
        assertEquals("testuser", user.get().getUsername());
        assertEquals("test@example.com", user.get().getEmail());
    }

    @Test
    void findById_nonExistingUser_returnsEmpty() {
        var user = userService.findById(99999L);
        
        assertFalse(user.isPresent());
    }

    @Test
    void getById_nonExistingUser_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.getById(99999L);
        });
        
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void findByUsername_existingUser_returnsUser() {
        var user = userService.findByUsername("testuser");
        
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
    }

    @Test
    void getByUsername_nonExistingUser_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.getByUsername("nonexistent");
        });
        
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void updateProfile_withNickname_updatesNickname() {
        var request = new UpdateUserRequest("New Nickname", null, null, null);
        
        var result = userService.updateProfile(testUser.getId(), request);
        
        assertEquals("New Nickname", result.nickname());
        assertEquals("testuser", result.username()); // Unchanged
    }

    @Test
    void updateProfile_withNewCurrency_updatesCurrency() {
        var request = new UpdateUserRequest(null, null, "EUR", null);
        
        var result = userService.updateProfile(testUser.getId(), request);
        
        assertEquals("EUR", result.defaultCurrency());
    }

    @Test
    void updateProfile_withNewEmail_updatesEmail() {
        var request = new UpdateUserRequest(null, "newemail@example.com", null, null);
        
        var result = userService.updateProfile(testUser.getId(), request);
        
        assertEquals("newemail@example.com", result.email());
    }

    @Test
    void updateProfile_withExistingEmail_throwsException() {
        // Create another user
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("hash");
        anotherUser.setSalt("salt");
        anotherUser.setEmailVerified(true);
        userRepository.save(anotherUser);
        
        // Try to update testUser's email to the existing email
        var request = new UpdateUserRequest(null, "another@example.com", null, null);
        
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.updateProfile(testUser.getId(), request);
        });
        
        assertEquals(ResultCode.USER_ALREADY_EXISTS.getCode(), ex.getErrorCode());
    }

    @Test
    void existsByUsername_existingUser_returnsTrue() {
        assertTrue(userService.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_nonExistingUser_returnsFalse() {
        assertFalse(userService.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail_existingUser_returnsTrue() {
        assertTrue(userService.existsByEmail("test@example.com"));
    }

    @Test
    void save_newUser_persistsAndRetrieves() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setNickname("New User");
        newUser.setPassword("hash");
        newUser.setSalt("salt");
        newUser.setEmailVerified(true);
        
        User saved = userRepository.save(newUser);
        assertNotNull(saved.getId());
        
        User retrieved = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("newuser", retrieved.getUsername());
    }

    @Test
    void deleteUser_userRemovedFromDatabase() {
        Long userId = testUser.getId();
        
        userRepository.deleteById(userId);
        
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void count_afterOperations_returnsCorrectCount() {
        long initialCount = userRepository.count();
        assertEquals(1, initialCount);
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("hash");
        user2.setSalt("salt");
        user2.setEmailVerified(true);
        userRepository.save(user2);
        
        assertEquals(2, userRepository.count());
    }
}