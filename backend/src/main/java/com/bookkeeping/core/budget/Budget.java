package com.bookkeeping.core.budget;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    /** Amount in cents */
    @Column(nullable = false)
    private Long amount;
    
    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;
    
    @Column(name = "budget_month", nullable = false)
    private Integer budgetMonth;
    
    @Column(name = "created_unix_time", nullable = false)
    private Long createdTime;
    
    @Column(name = "updated_unix_time")
    private Long updatedTime;
}