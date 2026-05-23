package com.bookkeeping.core.transaction;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Transaction extends BaseEntity {

    /** 1=MODIFY_BALANCE, 2=INCOME, 3=EXPENSE, 4=TRANSFER_OUT, 5=TRANSFER_IN */
    @Column(name = "transaction_type", nullable = false)
    private Integer transactionType;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "category_id")
    private Long categoryId;

    /** Amount in cents */
    @Column(nullable = false)
    private Long amount;

    @Column(length = 255)
    private String description;

    @Column(name = "transaction_time", nullable = false)
    private Long transactionTime;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tag_ids", columnDefinition = "TEXT")
    private String tagIds;
}
