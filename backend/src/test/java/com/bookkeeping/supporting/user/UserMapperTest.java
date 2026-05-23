package com.bookkeeping.supporting.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserMapper (MapStructPlus generated converter).
 * Uses plain unit test - no Spring context needed.
 * 
 * The mapper is generated from @MapperAuto on UserDto record:
 * - @MapperAuto(sourceEntity = User.class, direction = Direction.From)
 * - Generates: UserDtoMapperConverter implements UserMapper
 * - Also generates: UserMapper interface with toDto(User) method
 */
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserDtoMapperConverter();
    }

    @Test
    void toDto_withAllFields_mapsCorrectly() {
        User user = createTestUser();

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("testuser", dto.username());
        assertEquals("test@example.com", dto.email());
        assertEquals("Test User", dto.nickname());
        assertEquals("USD", dto.defaultCurrency());
        assertEquals(100L, dto.defaultAccountId());
        assertEquals("en-US", dto.language());
    }

    @Test
    void toDto_withNullUser_returnsNull() {
        UserDto dto = userMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_withNullFields_handlesGracefully() {
        User user = createTestUser().toBuilder().nickname(null).defaultAccountId(null).language(null).build().withId(1L);

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals("testuser", dto.username());
        assertNull(dto.nickname());
        assertNull(dto.defaultAccountId());
        assertNull(dto.language());
    }

    @Test
    void toDto_withEmptyStrings_mapsCorrectly() {
        User user = createTestUser().toBuilder().nickname("").defaultCurrency("").language("").build().withId(1L);

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals("", dto.nickname());
        assertEquals("", dto.defaultCurrency());
        assertEquals("", dto.language());
    }

    @Test
    void toDto_usernameAndEmailRequired_fieldsSet() {
        User user = createTestUser();

        UserDto dto = userMapper.toDto(user);

        assertEquals(user.getUsername(), dto.username());
        assertEquals(user.getEmail(), dto.email());
    }

    @Test
    void toDto_preservesAllStringValues() {
        User user = createTestUser().toBuilder()
                .username("john_doe")
                .email("john@example.com")
                .nickname("John Doe")
                .defaultCurrency("EUR")
                .language("de-DE")
                .build().withId(1L);

        UserDto dto = userMapper.toDto(user);

        assertEquals("john_doe", dto.username());
        assertEquals("john@example.com", dto.email());
        assertEquals("John Doe", dto.nickname());
        assertEquals("EUR", dto.defaultCurrency());
        assertEquals("de-DE", dto.language());
    }

    @Test
    void toDto_withSpecialCharacters_handlesCorrectly() {
        User user = createTestUser().toBuilder()
                .username("user+special")
                .email("user+tag@example.com")
                .nickname("User <Special> & Co.")
                .build().withId(1L);

        UserDto dto = userMapper.toDto(user);

        assertEquals("user+special", dto.username());
        assertEquals("user+tag@example.com", dto.email());
        assertEquals("User <Special> & Co.", dto.nickname());
    }

    @Test
    void toDto_longValues_mapsCorrectly() {
        String longUsername = "a".repeat(32);
        String longEmail = "user@" + "x".repeat(85) + ".com";
        String longNickname = "N".repeat(64);
        
        User user = createTestUser().toBuilder()
                .username(longUsername)
                .email(longEmail)
                .nickname(longNickname)
                .build().withId(1L);

        UserDto dto = userMapper.toDto(user);

        assertEquals(longUsername, dto.username());
        assertEquals(longEmail, dto.email());
        assertEquals(longNickname, dto.nickname());
    }

    @Test
    void toDto_currencyCodes_mapsCorrectly() {
        String[] currencies = {"USD", "EUR", "CNY", "JPY", "GBP", "AUD"};
        
        for (String currency : currencies) {
            User user = createTestUser().toBuilder().defaultCurrency(currency).build().withId(1L);
            UserDto dto = userMapper.toDto(user);
            assertEquals(currency, dto.defaultCurrency(), 
                "Currency " + currency + " should map correctly");
        }
    }

    @Test
    void toDto_languageCodes_mapsCorrectly() {
        String[] languages = {"en-US", "zh-CN", "ja-JP", "de-DE", "fr-FR"};
        
        for (String language : languages) {
            User user = createTestUser().toBuilder().language(language).build().withId(1L);
            UserDto dto = userMapper.toDto(user);
            assertEquals(language, dto.language(),
                "Language " + language + " should map correctly");
        }
    }

    private User createTestUser() {
        return User.builder()
                
                .username("testuser")
                .email("test@example.com")
                .nickname("Test User")
                .password("hashedpassword")
                .salt("salt123")
                .defaultCurrency("USD")
                .defaultAccountId(100L)
                .language("en-US")
                .emailVerified(true)
                .disabled(false)
                .build().withId(1L);
    }
}
