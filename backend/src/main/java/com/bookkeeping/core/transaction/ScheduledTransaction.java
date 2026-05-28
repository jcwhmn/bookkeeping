package com.bookkeeping.core.transaction;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scheduled_transactions")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScheduledTransaction extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Transaction template
    @Column(name = "transaction_type", nullable = false)
    private Integer transactionType;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "tag_ids", length = 500)
    private String tagIds;

    // Schedule
    @Column(name = "frequency", length = 20, nullable = false)
    private String frequency;  // daily, weekly, monthly, yearly

    @Column(name = "interval_days")
    @Builder.Default
    private Integer intervalDays = 1;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;  // 0=Sunday, 1=Monday...

    @Column(name = "day_of_month")
    private Integer dayOfMonth;  // 1-31, -1=last day

    @Column(name = "month_of_year")
    private Integer monthOfYear;  // 1-12

    // Timing
    @Column(name = "start_date", nullable = false)
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    @Column(name = "next_run_time", nullable = false)
    private Long nextRunTime;

    // Status
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "last_run_time")
    private Long lastRunTime;

    @Column(name = "last_run_result", length = 50)
    private String lastRunResult;

    @Column(name = "run_count")
    @Builder.Default
    private Integer runCount = 0;

    // Soft delete
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private Long deletedAt;

    // Setters
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setDeletedAt(Long deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNextRunTime(Long nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public void setLastRunTime(Long lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public void setLastRunResult(String lastRunResult) {
        this.lastRunResult = lastRunResult;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public void incrementRunCount() {
        this.runCount = (this.runCount == null ? 0 : this.runCount) + 1;
    }

    // Additional setters for update operations
    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMonthOfYear(Integer monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}