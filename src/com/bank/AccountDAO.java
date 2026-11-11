package com.bank;

import java.sql.SQLException;
import  java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.math.BigDecimal;
public class AccountDAO {
    public Account getAccountByUserId(int userId) {

        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Account(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getInt("user_id"),
                            rs.getString("account_type"),
                            rs.getBigDecimal("balance"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Account Error: " + e.getMessage());
        }

        return null;
    }

    // deposit method
    public boolean deposit(int userId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid deposit amount!");
            return false;
        }

        String updateSql = "UPDATE accounts " +
                "SET balance = balance + ? " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        String selectSql = "SELECT id, balance FROM accounts " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        String insertSql = "INSERT INTO transactions " +
                "(account_id, transaction_type, amount, balance_after, description) " +
                "VALUES (?, 'DEPOSIT', ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int accountId;
                BigDecimal newBalance;

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, userId);

                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        accountId = rs.getInt("id");
                        newBalance = rs.getBigDecimal("balance");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, accountId);
                    ps.setBigDecimal(2, amount);
                    ps.setBigDecimal(3, newBalance);
                    ps.setString(4, "Money deposited into account");

                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Deposit Error: " + e.getMessage());
            return false;
        }
    }


    //create account method
    public boolean createAccount(int userId, String accountNumber,
                                 String accountType, String pinHash,
                                 String pinSalt) {

        String sql = "INSERT INTO accounts " +
                "(account_number, user_id, account_type, balance, " +
                "status, pin_hash, pin_salt) " +
                "VALUES (?, ?, ?, 0.00, 'ACTIVE', ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            ps.setInt(2, userId);
            ps.setString(3, accountType);
            ps.setString(4, pinHash);
            ps.setString(5, pinSalt);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Account Creation Error: " + e.getMessage());
            return false;
        }
    }

    private String generateAccountNumber() {
        return "BANK" + System.currentTimeMillis();
    }

    public int findAccountIdByUserId(int userId) {

        String sql = "SELECT id FROM accounts WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            System.out.println("Account Fetch Error: " + e.getMessage());
        }

        return -1;
    }


    //withdraw method
    public boolean withdraw(int userId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid withdrawal amount!");
            return false;
        }

        String selectSql = "SELECT id, balance FROM accounts " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        String updateSql = "UPDATE accounts SET balance = balance - ? " +
                "WHERE id = ? AND balance >= ?";

        String insertSql = "INSERT INTO transactions " +
                "(account_id, transaction_type, amount, balance_after, description) " +
                "VALUES (?, 'WITHDRAWAL', ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int accountId;
                BigDecimal currentBalance;

                // Step 1: Fetch account details
                try (PreparedStatement ps = con.prepareStatement(selectSql)) {

                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Active account not found!");
                            return false;
                        }

                        accountId = rs.getInt("id");
                        currentBalance = rs.getBigDecimal("balance");
                    }
                }

                // Step 2: Check sufficient balance
                if (currentBalance.compareTo(amount) < 0) {
                    con.rollback();
                    System.out.println("Insufficient balance!");
                    return false;
                }

                // Step 3: Update account balance
                BigDecimal newBalance = currentBalance.subtract(amount);

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {

                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, accountId);
                    ps.setBigDecimal(3, amount);

                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        System.out.println("Withdrawal failed!");
                        return false;
                    }
                }

                // Step 4: Insert transaction record
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                    ps.setInt(1, accountId);
                    ps.setBigDecimal(2, amount);
                    ps.setBigDecimal(3, newBalance);
                    ps.setString(4, "Money withdrawn from account");

                    ps.executeUpdate();
                }

                // Step 5: Commit transaction
                con.commit();

                System.out.println("Amount withdrawn successfully!");
                System.out.println("Remaining Balance: " + newBalance);

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Withdrawal Error: " + e.getMessage());
            return false;
        }
    }

    //AccountBalance
    public BigDecimal getAccountBalance(int userId) {

        String sql = "SELECT balance FROM accounts " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }

        } catch (SQLException e) {
            System.out.println("Balance Fetch Error: " + e.getMessage());
        }

        return null;
    }

//transfer Money
    public boolean transferMoney(int senderUserId,
                                 String receiverAccountNumber,
                                 BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount!");
            return false;
        }

        String senderSql = "SELECT id FROM accounts " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        String receiverSql = "SELECT id FROM accounts " +
                "WHERE account_number = ? AND status = 'ACTIVE'";

        String lockSql = "SELECT id, balance FROM accounts " +
                "WHERE id IN (?, ?) ORDER BY id FOR UPDATE";

        String updateSql = "UPDATE accounts SET balance = ? WHERE id = ?";

        String insertSql = "INSERT INTO transactions " +
                "(account_id, related_account_id, transaction_type, " +
                "amount, balance_after, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int senderId;
                int receiverId;

                // Find sender account
                try (PreparedStatement ps = con.prepareStatement(senderSql)) {
                    ps.setInt(1, senderUserId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Sender account not found!");
                            return false;
                        }
                        senderId = rs.getInt("id");
                    }
                }

                // Find receiver account
                try (PreparedStatement ps = con.prepareStatement(receiverSql)) {
                    ps.setString(1, receiverAccountNumber);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Receiver account not found!");
                            return false;
                        }
                        receiverId = rs.getInt("id");
                    }
                }

                if (senderId == receiverId) {
                    con.rollback();
                    System.out.println("You cannot transfer money to your own account!");
                    return false;
                }

                BigDecimal senderBalance = null;
                BigDecimal receiverBalance = null;

                // Lock both accounts and fetch balances
                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");

                            if (id == senderId) {
                                senderBalance = rs.getBigDecimal("balance");
                            } else if (id == receiverId) {
                                receiverBalance = rs.getBigDecimal("balance");
                            }
                        }
                    }
                }

                if (senderBalance == null || receiverBalance == null) {
                    con.rollback();
                    System.out.println("Account not available!");
                    return false;
                }

                if (senderBalance.compareTo(amount) < 0) {
                    con.rollback();
                    System.out.println("Insufficient balance!");
                    return false;
                }

                BigDecimal newSenderBalance = senderBalance.subtract(amount);
                BigDecimal newReceiverBalance = receiverBalance.add(amount);

                // Update sender balance
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, newSenderBalance);
                    ps.setInt(2, senderId);
                    ps.executeUpdate();
                }

                // Update receiver balance
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setBigDecimal(1, newReceiverBalance);
                    ps.setInt(2, receiverId);
                    ps.executeUpdate();
                }

                // Record sender transaction
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);
                    ps.setString(3, "TRANSFER_OUT");
                    ps.setBigDecimal(4, amount);
                    ps.setBigDecimal(5, newSenderBalance);
                    ps.setString(6, "Money transferred to " + receiverAccountNumber);
                    ps.executeUpdate();
                }

                // Record receiver transaction
                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, receiverId);
                    ps.setInt(2, senderId);
                    ps.setString(3, "TRANSFER_IN");
                    ps.setBigDecimal(4, amount);
                    ps.setBigDecimal(5, newReceiverBalance);
                    ps.setString(6, "Money received from account ID " + senderId);
                    ps.executeUpdate();
                }

                con.commit();

                System.out.println("Money transferred successfully!");
                System.out.println("Remaining Balance: Rs. " + newSenderBalance);

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Transfer Error: " + e.getMessage());
            return false;
        }
    }

    //show Account details
    public void showAccountDetails(int userId) {

        String sql = "SELECT account_number, account_type, balance, status " +
                "FROM accounts WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("\n===== ACCOUNT DETAILS =====");
                    System.out.println("Account Number: " +
                            rs.getString("account_number"));
                    System.out.println("Account Type: " +
                            rs.getString("account_type"));
                    System.out.println("Available Balance: Rs. " +
                            rs.getBigDecimal("balance"));
                    System.out.println("Account Status: " +
                            rs.getString("status"));
                } else {
                    System.out.println("Account not found!");
                }

            }

        } catch (SQLException e) {
            System.out.println("Account Details Error: " + e.getMessage());
        }
    }


}
