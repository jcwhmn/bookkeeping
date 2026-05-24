package com.bookkeeping.core.token;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Token extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    @Column(name = "token_type", nullable = false)
    private Integer tokenType; // 1=normal, 5=MCP, 8=API

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "last_seen")
    private Long lastSeen;

    @Column(name = "expires_at")
    private Long expiresAt;

    @Column(name = "is_current")
    private Boolean isCurrent = false;
}