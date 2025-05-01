package com.internlink.internlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    // General method to send any email
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    // Specific method for reset link
    public void sendResetLink(String to, String token) {
        String subject = "Password Reset Request";
        String text = "Use the following code to reset your password: " + token;
        sendEmail(to, subject, text); // calls method in the same class
    }

    // Specific method for general notifications
    public void sendNotification(String to, String messageBody) {
        String subject = "Notification from InternLink";
        sendEmail(to, subject, messageBody); // calls method in the same class
    }
}
