package com.bookkeeping.core.account;

public record AccountDto(
    String idStr,
    String name,
    String type,
    String currency,
    String balanceStr,
    String icon,
    String color,
    String notes,
    String includeInTotalStr,
    String archivedStr
) {
    public static AccountDto fromEntity(Account account) {
        return new AccountDto(
            account.getId() != null ? account.getId().toString() : null,
            account.getName(),
            account.getType() != null ? account.getType().name() : null,
            account.getCurrency(),
            account.getBalance() != null ? account.getBalance().toString() : "0",
            account.getIcon(),
            account.getColor(),
            account.getNotes(),
            account.getIncludeInTotal() != null ? account.getIncludeInTotal().toString() : "true",
            account.getArchived() != null ? account.getArchived().toString() : "false"
        );
    }
}