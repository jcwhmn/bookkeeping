package com.bookkeeping.core.category;

import com.bookkeeping.common.BaseEntity;
import com.bookkeeping.common.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "category_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hidden = false;

    @Column(length = 64)
    private String icon;

    @Column(length = 7)
    private String color;

    @Column(length = 255)
    private String comment;

    // No setters - use toBuilder() pattern for all updates:
    // category = category.toBuilder().name("new").icon("mdi-x").build();
}
