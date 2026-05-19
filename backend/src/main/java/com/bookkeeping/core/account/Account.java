package com.bookkeeping.core.account;

import com.bookkeeping.common.BaseEntity;
import com.bookkeeping.common.enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;
    
    @Column(nullable = false, length = 3)
    private String currency = "USD";
    
    @Column(nullable = false)
    private Long balance = 0L;
    
    @Column(length = 50)
    private String icon;
    
    @Column(length = 7)
    private String color;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "include_in_total")
    private Boolean includeInTotal = true;
    
    private Boolean archived = false;
}