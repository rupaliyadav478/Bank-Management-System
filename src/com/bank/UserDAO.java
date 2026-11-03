package com.bank;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
public class UserDAO {

    public boolean registerUser(User user) {

        String sql = "INSERT INTO users " +
                "(username, password_hash, salt, email, phone) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());

            // Temporary: BCrypt implementation is pending
            ps.setString(2, user.getPassword());

            // Temporary placeholder until password hashing is implemented
            ps.setString(3, "TEMP");

            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            null,
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return null;
    }
    public int getUserIdByUsername(String username) {

        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            System.out.println("User ID Error: " + e.getMessage());
        }

        return -1;
    }
}
