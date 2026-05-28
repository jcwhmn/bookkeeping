package com.bookkeeping.supporting.user;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service for Two-Factor Authentication (TOTP).
 * Implements RFC 6238 TOTP algorithm.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TwoFactorService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    private static final int TOTP_PERIOD = 30;      // seconds
    private static final int TOTP_DIGITS = 6;

    /**
     * Get current 2FA status for the logged-in user.
     */
    @Transactional(readOnly = true)
    public TwoFactorDtos.TwoFactorStatusResponse getStatus() {
        User user = securityUtils.requireCurrentUser();
        return new TwoFactorDtos.TwoFactorStatusResponse(
                Boolean.TRUE.equals(user.getTotpEnabled()),
                user.getTotpCreatedAt()
        );
    }

    /**
     * Request to enable 2FA - generates TOTP secret and QR code data URI.
     */
    public TwoFactorDtos.TwoFactorEnableResponse requestEnable() {
        User user = securityUtils.requireCurrentUser();
        
        // Generate a 20-byte secret (160 bits)
        byte[] secretBytes = new byte[20];
        new SecureRandom().nextBytes(secretBytes);
        String secret = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);
        
        // Generate QR code as data URI
        String qrData = String.format("otpauth://totp/Bookkeeping:%s?secret=%s&issuer=Bookkeeping&digits=%d&period=%d",
                user.getUsername(), secret, TOTP_DIGITS, TOTP_PERIOD);
        String qrcode = "data:text/plain;base64," + java.util.Base64.getEncoder().encodeToString(qrData.getBytes());
        
        return new TwoFactorDtos.TwoFactorEnableResponse(secret, qrcode);
    }

    /**
     * Confirm 2FA setup with TOTP passcode.
     */
    public TwoFactorDtos.TwoFactorEnableConfirmResponse confirmEnable(
            String secret, String passcode) {
        User user = securityUtils.requireCurrentUser();
        
        // Verify the passcode
        if (!validateCode(secret, passcode)) {
            throw new BusinessException(ResultCode.INVALID_TOTP_PASSCODE);
        }
        
        // Generate recovery codes
        java.util.List<String> recoveryCodes = generateRecoveryCodes();
        
        // Update user directly (not via toBuilder)
        user.setTotpSecret(secret);
        user.setTotpEnabled(true);
        user.setTotpCreatedAt(System.currentTimeMillis() / 1000);
        user.setRecoveryCodes(String.join(",", recoveryCodes));
        userRepository.save(user);
        
        return new TwoFactorDtos.TwoFactorEnableConfirmResponse(null, recoveryCodes);
    }

    /**
     * Disable 2FA with password verification.
     */
    public void disable(String password) {
        User user = securityUtils.requireCurrentUser();
        
        // Verify password
        if (!verifyPassword(user, password)) {
            throw new BusinessException(ResultCode.PASSWORD_INCORRECT);
        }
        
        // Disable 2FA
        user.setTotpSecret(null);
        user.setTotpEnabled(false);
        user.setTotpCreatedAt(null);
        user.setRecoveryCodes(null);
        userRepository.save(user);
    }

    /**
     * Regenerate recovery codes (requires password).
     */
    public java.util.List<String> regenerateRecoveryCodes(String password) {
        User user = securityUtils.requireCurrentUser();
        
        // Verify password
        if (!verifyPassword(user, password)) {
            throw new BusinessException(ResultCode.PASSWORD_INCORRECT);
        }
        
        // Generate new recovery codes
        java.util.List<String> recoveryCodes = generateRecoveryCodes();
        
        // Update user
        user.setRecoveryCodes(String.join(",", recoveryCodes));
        userRepository.save(user);
        
        return recoveryCodes;
    }

    /**
     * Validate a TOTP passcode for login verification.
     */
    public boolean validatePasscode(String passcode) {
        User user = securityUtils.requireCurrentUser();
        if (!Boolean.TRUE.equals(user.getTotpEnabled()) || user.getTotpSecret() == null) {
            return false;
        }
        
        return validateCode(user.getTotpSecret(), passcode);
    }

    /**
     * Consume a recovery code (one-time use).
     */
    public boolean consumeRecoveryCode(String code) {
        User user = securityUtils.requireCurrentUser();
        if (user.getRecoveryCodes() == null) {
            return false;
        }
        
        String[] codes = user.getRecoveryCodes().split(",");
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(code)) {
                // Remove the used code
                codes[i] = null;
                String remaining = java.util.Arrays.stream(codes)
                        .filter(c -> c != null)
                        .collect(Collectors.joining(","));
                
                user.setRecoveryCodes(remaining.isEmpty() ? null : remaining);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // ============ Private Helpers ============

    /**
     * Validate a TOTP code per RFC 6238.
     */
    private boolean validateCode(String secret, String code) {
        try {
            byte[] key = java.util.Base64.getUrlDecoder().decode(secret);
            long counter = System.currentTimeMillis() / 1000 / TOTP_PERIOD;
            
            // Check current and adjacent time steps (allow 1 step tolerance)
            for (int offset = -1; offset <= 1; offset++) {
                String expected = generateTOTP(key, counter + offset);
                if (expected.equals(code)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate TOTP per RFC 6238.
     */
    private String generateTOTP(byte[] key, long counter) throws Exception {
        // Convert counter to 8-byte big-endian
        byte[] counterBytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            counterBytes[i] = (byte) (counter & 0xff);
            counter >>= 8;
        }
        
        // HMAC-SHA1
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "HmacSHA1"));
        byte[] hash = mac.doFinal(counterBytes);
        
        // Dynamic truncation
        int offset = hash[hash.length - 1] & 0x0f;
        int binary = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);
        
        // Generate OTP
        int otp = binary % (int) Math.pow(10, TOTP_DIGITS);
        return String.format("%0" + TOTP_DIGITS + "d", otp);
    }

    private java.util.List<String> generateRecoveryCodes() {
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 8)
                .mapToObj(i -> {
                    byte[] bytes = new byte[8];
                    random.nextBytes(bytes);
                    return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
                            .substring(0, 8).toUpperCase();
                })
                .collect(Collectors.toList());
    }

    private boolean verifyPassword(User user, String password) {
        // Simple check using the same logic as auth (MD5 with salt prefix)
        try {
            String salted = user.getSalt() + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest(salted.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) { sb.append(String.format("%02x", b)); }
            return sb.toString().equals(user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
}