package com.bank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private long id;
    private int accountId;
    private Integer relatedAccountId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    public Transaction(long id, int accountId,
                       Integer relatedAccountId,
                       String transactionType,
                       BigDecimal amount,
                       BigDecimal balanceAfter,
                       String description,
                       LocalDateTime createdAt) {

        this.id = id;
        this.accountId = accountId;
        this.relatedAccountId = relatedAccountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
