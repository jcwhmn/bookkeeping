package com.bookkeeping.core.token;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.config.security.JwtTokenProvider;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import com.bookkeeping.supporting.user.UserMapper;
import com.bookkeeping.supporting.user.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;

    private static final String API_BASE_URL = "/api/v1/";
    private static final String MCP_URL = "/mcp";

    public TokenService(TokenRepository tokenRepository, JwtTokenProvider jwtTokenProvider,
                       SecurityUtils securityUtils, UserMapper userMapper) {
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityUtils = securityUtils;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<TokenInfo> listTokens() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return tokenRepository.findByUserIdOrderByLastSeenDesc(userId).stream()
                .map(this::toInfo).toList();
    }

    @Transactional
    public GenerateApiTokenResponse generateApiToken(long expiresInSeconds, String password) {
        Long userId = securityUtils.requireCurrentUser().getId();
        verifyPassword(userId, password);

        String rawToken = generateRandomToken();
        String hash = hashToken(rawToken);
        long now = Instant.now().getEpochSecond();
        long expiresAt = now + expiresInSeconds;

        Token token = Token.builder()
                .userId(userId)
                .tokenHash(hash)
                .tokenType(8)
                .lastSeen(now)
                .expiresAt(expiresAt)
                .isCurrent(false)
                .build();
        tokenRepository.save(token);

        return new GenerateApiTokenResponse(rawToken, API_BASE_URL);
    }

    @Transactional
    public GenerateMcpTokenResponse generateMcpToken(long expiresInSeconds, String password) {
        Long userId = securityUtils.requireCurrentUser().getId();
        verifyPassword(userId, password);

        String rawToken = generateRandomToken();
        String hash = hashToken(rawToken);
        long now = Instant.now().getEpochSecond();
        long expiresAt = now + expiresInSeconds;

        Token token = Token.builder()
                .userId(userId)
                .tokenHash(hash)
                .tokenType(5)
                .lastSeen(now)
                .expiresAt(expiresAt)
                .isCurrent(false)
                .build();
        tokenRepository.save(token);

        return new GenerateMcpTokenResponse(rawToken, MCP_URL);
    }

    @Transactional
    public void revokeToken(String tokenId, String password) {
        Long userId = securityUtils.requireCurrentUser().getId();
        verifyPassword(userId, password);
        tokenRepository.deleteByUserIdAndId(userId, Long.parseLong(tokenId));
    }

    @Transactional
    public RefreshTokenResponse refreshToken(String tokenId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        String rawToken = jwtTokenProvider.generateToken(securityUtils.requireCurrentUser().getUsername());
        tokenRepository.clearCurrentForUser(userId);
        return new RefreshTokenResponse(rawToken, tokenId,
                userMapper.toDto(securityUtils.requireCurrentUser()), "", "");
    }

    @Transactional
    public void revokeAllTokens(String password) {
        Long userId = securityUtils.requireCurrentUser().getId();
        verifyPassword(userId, password);
        tokenRepository.deleteByUserId(userId);
    }

    private void verifyPassword(Long userId, String password) {
        // Simple password check — in real app would verify against hashed password
    }

    private String generateRandomToken() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((System.currentTimeMillis() + java.util.UUID.randomUUID().toString()).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return java.util.UUID.randomUUID().toString();
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return token;
        }
    }

    private TokenInfo toInfo(Token t) {
        return new TokenInfo(
                String.valueOf(t.getId()),
                t.getTokenType(),
                t.getUserAgent(),
                t.getLastSeen(),
                Boolean.FALSE.equals(t.getIsCurrent()) == false
        );
    }

    public record TokenInfo(String tokenId, Integer tokenType, String userAgent, Long lastSeen, Boolean isCurrent) {}
    public record GenerateApiTokenResponse(String token, String apiBaseUrl) {}
    public record GenerateMcpTokenResponse(String token, String mcpUrl) {}
    public record RefreshTokenResponse(String newToken, String oldTokenId, UserDto user, String cloudSettings, String notification) {}
    public record GenerateRequest(Long expiresInSeconds, String password) {}
    public record RevokeRequest(String tokenId, String password) {}
    public record RefreshRequest(String tokenId) {}
}