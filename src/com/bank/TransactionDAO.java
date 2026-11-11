package com.bank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public List<Transaction> getTransactionsByAccountId(int accountId) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions " +
                "WHERE account_id = ? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp timestamp = rs.getTimestamp("created_at");

                    Transaction transaction = new Transaction(
                            rs.getLong("id"),
                            rs.getInt("account_id"),
                            (Integer) rs.getObject("related_account_id"),
                            rs.getString("transaction_type"),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("balance_after"),
                            rs.getString("description"),
                            timestamp != null
                                    ? timestamp.toLocalDateTime()
                                    : null
                    );

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            System.out.println("Transaction History Error: " + e.getMessage());
        }

        return transactions;
    }

    public List<Transaction> getMiniStatementByAccountId(int accountId) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions " +
                "WHERE account_id = ? " +
                "ORDER BY created_at DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp timestamp = rs.getTimestamp("created_at");

                    Transaction transaction = new Transaction(
                            rs.getLong("id"),
                            rs.getInt("account_id"),
                            (Integer) rs.getObject("related_account_id"),
                            rs.getString("transaction_type"),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("balance_after"),
                            rs.getString("description"),
                            timestamp != null
                                    ? timestamp.toLocalDateTime()
                                    : null
                    );

                    transactions.add(transaction);
                }
            }

        } catch (SQLException e) {
            System.out.println("Mini Statement Error: " + e.getMessage());
        }

        return transactions;
    }

}
