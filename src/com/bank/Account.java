package com.bank;
import java.math.BigDecimal;
public class Account {
    private int id;
    private String accountNumber;
    private int userId;
    private String accountType;
    private BigDecimal balance;
    private String status;

    public Account(int id, String accountNumber, int userId,
                   String accountType, BigDecimal balance, String status) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getUserId() {
        return userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }
}
