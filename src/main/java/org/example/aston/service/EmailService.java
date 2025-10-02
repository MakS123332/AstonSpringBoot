package org.example.aston.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.concurrent.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, CircuitBreakerFactory circuitBreakerFactory) {
        this.mailSender = mailSender;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public void sendEmail(String to, String subject, String text) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("emailService");

        circuitBreaker.run(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                mailSender.send(message);
                return null;
            } catch (Exception e) {
                throw new EmailServiceException("Failed to send email: " + e.getMessage());
            }
        }, throwable -> {
            // Fallback logic
            log.error("Email service unavailable, message not sent to: {}", to);
            return null;
        });
    }
}

class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message) {
        super(message);
    }
}