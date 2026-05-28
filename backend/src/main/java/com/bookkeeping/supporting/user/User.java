package com.bookkeeping.supporting.user;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 64)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String salt;

    @Column(name = "default_currency", length = 3)
    private String defaultCurrency = "USD";

    @Column(name = "default_account_id")
    private Long defaultAccountId;

    @Column(length = 10)
    private String language = "en-US";

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    private Boolean disabled = false;

    @Column(length = 500)
    private String avatar;

    @Column(name = "week_start_day")
    private Integer firstDayOfWeek = 1;

    @Column(name = "fy_start_month")
    private Integer fiscalYearStart = 1;

    @Column(name = "date_format_string")
    private String dateFormat = "YYYY-MM-DD";

    @Column(name = "transaction_edit_scope")
    private Integer transactionEditScope = 1;

    @Column(name = "onboarding_completed")
    private Boolean onboardingCompleted = false;

    @Column(name = "totp_secret", length = 64)
    private String totpSecret;
    @Column(name = "totp_enabled")
    private Boolean totpEnabled = false;
    @Column(name = "totp_created_at")
    private Long totpCreatedAt;
    @Column(name = "recovery_codes", columnDefinition = "TEXT")
    private String recoveryCodes;
    
    // ============ 2FA Setters ============
    public void setTotpSecret(String totpSecret) { this.totpSecret = totpSecret; }
    public void setTotpEnabled(Boolean totpEnabled) { this.totpEnabled = totpEnabled; }
    public void setTotpCreatedAt(Long totpCreatedAt) { this.totpCreatedAt = totpCreatedAt; }
    public void setRecoveryCodes(String recoveryCodes) { this.recoveryCodes = recoveryCodes; }
    
    // ============ User Setters ============
    public void setOnboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }
    
    public boolean isActive() {
        return !Boolean.TRUE.equals(disabled);
    }
}