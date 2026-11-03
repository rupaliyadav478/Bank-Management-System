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
    public boolean deposit(int userId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid deposit amount!");
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance + ? " +
                "WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Deposit Error: " + e.getMessage());
        }

        return false;
    }
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
}
