package com.salesmate.controller;

import com.salesmate.configs.MailSender;
import com.salesmate.dao.UserDAO;
import com.salesmate.model.User;


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
    public boolean resetPassword(String email) {
        // Tạo mật khẩu mới
        String newPassword = generateNewPassword();

        // Cập nhật mật khẩu trong cơ sở dữ liệu
        boolean isUpdated = userDAO.updatePassword(email, newPassword);

        // Gửi email nếu mật khẩu được cập nhật thành công
        if (isUpdated) {
            MailSender mailSender = new MailSender();
            String subject = "Salmate - Mật khẩu mới";
            String content = "Mật khẩu mới của bạn là: " + newPassword + "\n"
                    + "Đăng nhập vào hệ thống và đổi mật khẩu ngay sau khi đăng nhập thành công!";
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

}
