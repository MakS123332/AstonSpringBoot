package org.example.aston.config;

import org.example.aston.entity.UserEvent;
import org.example.aston.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "user-events")
    public void handleUserEvent(UserEvent event) {
        String email = event.getEmail();
        String operation = event.getOperation();
        String subject = "Уведомление о аккаунте";
        String text;

        if ("CREATE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        } else if ("DELETE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            throw new IllegalArgumentException("Неизвестная операция: " + operation);
        }

        emailService.sendEmail(email, subject, text);
    }
}
