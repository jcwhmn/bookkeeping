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

    /** Parent account ID for sub-accounts; NULL for top-level accounts. */
    @Column(name = "parent_id")
    private Long parentId;

    /** Display order for drag-to-reorder. */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /** If true, account is hidden from UI. */
    @Column(nullable = false)
    private Boolean hidden = false;

    // === Setters for updates (no public setters in this project) ===
    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
