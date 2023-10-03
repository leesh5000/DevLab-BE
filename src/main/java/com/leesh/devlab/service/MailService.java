package com.leesh.devlab.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    private final JavaMailSender mailSender;
    private final String from;

    public MailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendMail(String to, String title, String content) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom(from);
        message.setSubject(title);
        message.setText(content);

        mailSender.send(message);
    }

}
