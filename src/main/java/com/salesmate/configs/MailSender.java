package com.salesmate.configs;

import java.io.FileInputStream; // Correct import statement
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private String host;
    private String user;
    private String pass;
    private String port;
    private String encryption;
    private String from;

    public MailSender() {
        loadConfig();
    }

    private void loadConfig() {
        FileInputStream inputStream = null;
        try {
            // Đọc cấu hình từ file config.properties
            Properties properties = new Properties();
            inputStream = new FileInputStream("config.properties");  // Đảm bảo đường dẫn chính xác
            properties.load(inputStream);
            host = properties.getProperty("EMAIL_HOST");
            user = properties.getProperty("EMAIL_USER");
            pass = properties.getProperty("EMAIL_PASS");
            port = properties.getProperty("EMAIL_PORT");
            encryption = properties.getProperty("EMAIL_ENCRYPTION");
            from = properties.getProperty("EMAIL_FROM");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendEmail(String to, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", encryption.equalsIgnoreCase("TLS"));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from.isEmpty() ? user : from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}