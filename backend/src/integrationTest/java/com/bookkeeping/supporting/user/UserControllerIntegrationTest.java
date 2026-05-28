package com.bookkeeping.supporting.user;

import com.bookkeeping.BaseIntegrationTest;
import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
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

    private User createTestUser(String username) {
        String salt = "salt123";
        String hashedPw = hashPassword("password123", salt);
        User user = User.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname("Test User")
                .password(hashedPw)
                .salt(salt)
                .defaultCurrency("USD")
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build();
        return userRepository.save(user);
    }

    private String hashPassword(String password, String salt) {
        try {
            String salted = salt + password;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(salted.getBytes(java.nio.charset.StandardCharsets.UTF_8));
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
    void findById_existingUser_returnsUser() {
        User user = createTestUser("findbyid_" + System.currentTimeMillis());
        
        var result = userService.findById(user.getId());
        
        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
        assertEquals(user.getEmail(), result.get().getEmail());
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
        String username = "findbyun_" + System.currentTimeMillis();
        createTestUser(username);
        
        var user = userService.findByUsername(username);
        
        assertTrue(user.isPresent());
        assertEquals(username + "@example.com", user.get().getEmail());
    }

    @Test
    void getByUsername_nonExistingUser_throwsBusinessException() {
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.getByUsername("nonexistent_" + System.currentTimeMillis());
        });
        
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), ex.getErrorCode());
    }

    @Test
    void updateProfile_withNickname_updatesNickname() {
        User user = createTestUser("updatenick_" + System.currentTimeMillis());
        var request = new UpdateUserRequest("New Nickname", null, null, null, null, 1, 1, 1, "YYYY-MM-DD");
        
        var result = userService.updateProfile(user.getId(), request);
        
        assertEquals("New Nickname", result.nickname());
        assertEquals(user.getUsername(), result.username());
    }

    @Test
    void updateProfile_withNewCurrency_updatesCurrency() {
        User user = createTestUser("updatecurr_" + System.currentTimeMillis());
        var request = new UpdateUserRequest(null, "EUR", null, null, null, 1, 1, 1, "YYYY-MM-DD");
        
        var result = userService.updateProfile(user.getId(), request);
        
        assertEquals("EUR", result.defaultCurrency());
    }

    @Test
    void updateProfile_withNewLanguage_updatesLanguage() {
        User user = createTestUser("updatelang_" + System.currentTimeMillis());
        var request = new UpdateUserRequest(null, null, "zh-CN", null, null, 1, 1, 1, "YYYY-MM-DD");
        
        var result = userService.updateProfile(user.getId(), request);
        
        assertEquals("zh-CN", result.language());
    }

    @Test
    void existsByUsername_existingUser_returnsTrue() {
        String username = "existsun_" + System.currentTimeMillis();
        createTestUser(username);
        
        assertTrue(userService.existsByUsername(username));
    }

    @Test
    void existsByUsername_nonExistingUser_returnsFalse() {
        assertFalse(userService.existsByUsername("nonexistent_" + System.currentTimeMillis()));
    }

    @Test
    void existsByEmail_existingUser_returnsTrue() {
        String username = "existsemail_" + System.currentTimeMillis();
        User user = createTestUser(username);
        
        assertTrue(userService.existsByEmail(user.getEmail()));
    }

    @Test
    void save_newUser_persistsAndRetrieves() {
        String username = "newuser_" + System.currentTimeMillis();
        User newUser = User.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname("New User")
                .password("hash")
                .salt("salt")
                .emailVerified(true)
                .build();
        
        User saved = userRepository.save(newUser);
        assertNotNull(saved.getId());
        
        User retrieved = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals(username, retrieved.getUsername());
    }

    @Test
    void deleteUser_userRemovedFromDatabase() {
        User user = createTestUser("deleteuser_" + System.currentTimeMillis());
        Long userId = user.getId();
        
        userRepository.deleteById(userId);
        
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void count_afterOperations_returnsCorrectCount() {
        long initialCount = userRepository.count();
        
        createTestUser("countuser1_" + System.currentTimeMillis());
        createTestUser("countuser2_" + System.currentTimeMillis());
        
        assertEquals(initialCount + 2, userRepository.count());
    }
}