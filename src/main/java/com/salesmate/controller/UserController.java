package com.salesmate.controller;

import com.salesmate.configs.MailSender;
import com.salesmate.dao.UserDAO;
import com.salesmate.model.User;
import java.util.List;


public class UserController {

    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    // Hàm đăng nhập với email và password
    public User login(String email, String password) {

        return userDAO.login(email, password);
    }

    // Hàm đăng nhập bằng FaceID

    public User getUserByAvatar(String avatar) {
        return userDAO.getUserByAvatar(avatar);
    }

    // Hàm thay đổi mật khẩu
    public boolean resetPassword(String email, String oldPassword, String newPassword) {
        // Call the correct updatePassword method with three arguments
        return userDAO.updatePassword(email, oldPassword, newPassword);
    }

    public boolean resetPassword(String email) {
        // Generate a new password
        String newPassword = generateNewPassword();

        // Update the password in the database
        boolean isUpdated = userDAO.updatePassword(email, newPassword);

        // Send an email with the new password if the update was successful
        if (isUpdated) {
            MailSender mailSender = new MailSender();
            String subject = "SalesMate - New Password";
            String content = "Your new password is: " + newPassword + "\n"
                    + "Please log in and change your password immediately.";
            mailSender.sendEmail(email, subject, content);
        }

        return isUpdated;
    }

    // Tạo mật khẩu mới dạng 123456 + 3 chữ số ngẫu nhiên
    private String generateNewPassword() {
        String basePassword = "123456";
        int randomNumber = (int) (Math.random() * 900) + 100; // Tạo số ngẫu nhiên 3 chữ số
        return basePassword + randomNumber;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    // Lấy thông tin người dùng theo ID
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
 // Đếm số lượng user
    public int countUser() {
        try {
            return userDAO.countUser();
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error
        }
    }

    public java.util.List<com.salesmate.model.ChartDataModel> getUserCountByRole() {
        com.salesmate.dao.UserDAO dao = new com.salesmate.dao.UserDAO();
        return dao.getUserCountByRole();
    }
    
     //  Thêm người dùng mới
    public boolean addUser(User user) {
        return userDAO.addUser(user);
    }

    //  softdelete người dùng theo ID
   public boolean deleteUserById(int userId) {
        return userDAO.deleteUserById(userId);
    }
// Lấy toàn bộ danh sách người dùng
   public List<User> getAllUsers() {
    return userDAO.getAllUsers();
}
// Tìm kiếm người dùng theo tên (keyword LIKE)
public List<User> searchUsersByName(String keyword) {
    return userDAO.searchUsersByName(keyword);
}
 //] Lọc người dùng theo vai trò (role)
 public List<User> filterUsersByRole(String role) {
        if (!role.equals("Manager") && !role.equals("Warehouse") && !role.equals("Sales")) {
            return getAllUsers();
        }
        return userDAO.filterUsersByRole(role);
    }

}
