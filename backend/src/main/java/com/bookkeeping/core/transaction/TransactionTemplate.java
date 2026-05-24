package com.bookkeeping.core.transaction;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_templates")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransactionTemplate extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_type", nullable = false)
    private Integer templateType = 1;

    @Column(length = 64)
    private String name;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    @Column(name = "source_amount")
    private Long sourceAmount;

    @Column(name = "destination_amount")
    private Long destinationAmount;

    @Column(name = "hide_amount")
    private Boolean hideAmount = false;

    @Column(name = "transaction_type", nullable = false)
    private Integer transactionType;

    @Column(length = 255)
    private String description;

    @Column(name = "tag_ids")
    private String tagIds;

    @Column(name = "display_order")
    private Integer displayOrder;

    private Boolean hidden = false;
}