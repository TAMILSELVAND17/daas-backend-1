package com.daascmputers.website.tools;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EmailService {
    private static final String fromMail = "tamild17042003@gmail.com";

    private static final String toMail = "oniononion9345@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

//    @Value("${spring.mail.username}")
//    private String fromEmail;

    @Async
    public void sendEmail(String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toMail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("Email sent successfully to {}", toMail);
        } catch (Exception ex) {
            log.error("Email not sent uccessfully to {}", toMail, ex);
        }
    }

    @Async
    public void sendEmail(String toUserMail, String userSubject, String userBody) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromMail);
        mailMessage.setTo(toUserMail);
        mailMessage.setSubject(userSubject);
        mailMessage.setText(userBody);

        try {
            mailSender.send(mailMessage);
            log.info("Email sent successfully to {}", toUserMail);
        } catch (Exception ex) {
            log.error("Email not sent uccessfully to {}", toUserMail, ex);
        }
    }

    @Async
    public void sendEmail(String subject){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(toMail);
        message.setTo(toMail);
        message.setSubject("Product Interest Notification from a Customer – Daas Computers");
        message.setText(subject);

        try {
            mailSender.send(message);
            log.info("Customer Interest sent successfully  ");


        } catch (Exception ex) {
            log.error("Customer Interest not sent uccessfully", ex);
        }
    }
}