package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.salesmate.configs.DBConnection;
import com.salesmate.model.User;

public class UserDAO {

    // Hàm kiểm tra đăng nhập
    public User login(String email, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE email = ? AND password = ?"; // Kiểm tra với email và password

        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login success");
                user = new User();
                user.setUsersId(rs.getInt("users_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setAvatar(rs.getString("avatar"));
                user.setEmail(rs.getString("email"));
                user.setStatus(rs.getString("status"));
                user.setPassword(rs.getString("password")); // Ensure password is set
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // Phương thức gửi mật khẩu mới
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        if (!isPasswordCorrect(email, oldPassword)) {
            return false; // Old password is incorrect
        }

        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isPasswordCorrect(String email, String oldPassword) {
        String query = "SELECT 1 FROM users WHERE email = ? AND password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, oldPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Return true if a record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByAvatar(String avatar) {
        User user = null;
        String query = "SELECT * FROM users WHERE avatar = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, avatar);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUsersId(rs.getInt("users_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setAvatar(rs.getString("avatar"));
                user.setEmail(rs.getString("email"));
                user.setStatus(rs.getString("status"));
                user.setPassword(rs.getString("password")); // Ensure password is set
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE users SET username = ?, email = ?, status = ? WHERE users_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getStatus());
            stmt.setInt(4, user.getUsersId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user by ID
    public User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM users WHERE users_id = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUsersId(rs.getInt("users_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setAvatar(rs.getString("avatar"));
                user.setEmail(rs.getString("email"));
                user.setStatus(rs.getString("status"));
                user.setPassword(rs.getString("password")); // Ensure password is set
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
