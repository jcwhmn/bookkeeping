package com.bookkeeping.supporting.user;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 64)
    private String nickname;
    
    @Column(nullable = false, length = 64)
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
}