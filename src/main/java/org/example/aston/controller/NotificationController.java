package org.example.aston.controller;

import org.example.aston.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestParam String email, @RequestParam String operation) {
        String subject = "Уведомление о аккаунте";
        String text;

        if ("CREATE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        } else if ("DELETE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return ResponseEntity.badRequest().body("Неизвестная операция: " + operation);
        }

        emailService.sendEmail(email, subject, text);
        return ResponseEntity.ok("Email отправлен");
    }
}
