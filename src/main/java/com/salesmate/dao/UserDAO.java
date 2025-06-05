package com.salesmate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    String role = user.getRole();
    if (!role.equals("Manager") && !role.equals("Warehouse") && !role.equals("Sales")) {
        throw new IllegalArgumentException("Invalid role: " + role);
    }
    String query =
        "UPDATE users SET " +
        "username = ?, " +
        "email    = ?, " +
        "status   = ?, " +
        "avatar   = ?, " +
        "role     = ? " +
        "WHERE users_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getStatus());
        stmt.setString(4, user.getAvatar());
        stmt.setString(5, user.getRole());
        stmt.setInt(6, user.getUsersId());

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
     // Đếm số lượng user
public int countUser() {
    String sql = "SELECT COUNT(*) FROM users";
    try (Connection connection = DBConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1); // Trả về số lượng user
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0; // Nếu có lỗi hoặc không tìm thấy dữ liệu, trả về 0
}

    // Thống kê số lượng user theo role
    public List<com.salesmate.model.ChartDataModel> getUserCountByRole() {
        List<com.salesmate.model.ChartDataModel> result = new ArrayList<>();
        String sql = "SELECT role, COUNT(*) AS count FROM users GROUP BY role";
        try (Connection conn = com.salesmate.configs.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new com.salesmate.model.ChartDataModel(rs.getString("role"), java.math.BigDecimal.valueOf(rs.getInt("count"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // NEW: Thêm người dùng
public boolean addUser(User user) {
    String role = user.getRole();
    if (!role.equals("Manager") && !role.equals("Warehouse") && !role.equals("Sales")) {
        throw new IllegalArgumentException("Invalid role: " + role);
    }
    String sql =
        "INSERT INTO users " +
        "(username, email, password, role, status, avatar, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getRole());
        stmt.setString(5, user.getStatus());
        stmt.setString(6, user.getAvatar());
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

  

    
     // Soft‐delete user: doi status thanh  'Inactive'
    public boolean deleteUserById(int userId) {
        String sql = "UPDATE users SET status = 'Inactive' WHERE users_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // NEW: Lấy danh sách toàn bộ user
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // NEW: Tìm kiếm user theo tên
    public List<User> searchUsersByName(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // NEW: Lọc user theo role
    public List<User> filterUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Hàm tiện ích tái sử dụng đọc từ ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsersId(rs.getInt("users_id"));
        user.setUsername(rs.getString("username"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setAvatar(rs.getString("avatar"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getString("status"));
        user.setPassword(rs.getString("password"));
        return user;
    }

}
