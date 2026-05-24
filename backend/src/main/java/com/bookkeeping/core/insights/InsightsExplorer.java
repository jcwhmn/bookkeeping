package com.bookkeeping.core.insights;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "insights_explorers")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InsightsExplorer extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 64)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(name = "display_order")
    private Integer displayOrder;

    private Boolean hidden = false;
}