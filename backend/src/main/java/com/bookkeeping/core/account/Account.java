package com.bookkeeping.core.account;

import com.bookkeeping.common.BaseEntity;
import com.bookkeeping.common.enums.AccountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Account entity for storing bookkeeping accounts.
 * Stores balance in cents (BIGINT) to avoid floating point issues.
 */
@Entity
@Table(name = "accounts")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "account_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    /**
     * Balance in cents (fen). Positive amounts are stored as BIGINT.
     * Frontend divides by 100 to display.
     */
    @Column(nullable = false)
    private Long balance = 0L;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 255)
    private String description;

    @Column
    private Boolean deleted = false;
}
