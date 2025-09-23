package org.example.aston.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.models.info.Contact;
import org.example.aston.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Отправить email уведомление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @PostMapping("/email")
    public ResponseEntity<EntityModel<String>> sendEmail(
            @Parameter(description = "Email адрес получателя") @RequestParam String email,
            @Parameter(description = "Тип операции: CREATE или DELETE") @RequestParam String operation) {

        String subject = "Уведомление о аккаунте";
        String text;

        if ("CREATE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        } else if ("DELETE".equals(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return ResponseEntity.badRequest().body(
                    EntityModel.of("Неизвестная операция: " + operation)
            );
        }

        emailService.sendEmail(email, subject, text);


        EntityModel<String> resource = EntityModel.of("Email отправлен");
        resource.add(linkTo(methodOn(NotificationController.class).sendEmail(email, operation)).withSelfRel());
        resource.add(linkTo(methodOn(NotificationController.class).getApiInfo()).withRel("api-info"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Получить информацию об API")
    @GetMapping("/info")
    public ResponseEntity<EntityModel<ApiInfo>> getApiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Notification Service API",
                "API для отправки email уведомлений",
                "1.0",
                "Условия использования",
                new Contact(),
                "Лицензия API",
                "https://example.com/license",
                Collections.emptyList()
        );

        EntityModel<ApiInfo> resource = EntityModel.of(apiInfo);
        resource.add(linkTo(methodOn(NotificationController.class).getApiInfo()).withSelfRel());
        resource.add(linkTo(methodOn(NotificationController.class).sendEmail("example@email.com", "CREATE")).withRel("send-email"));

        return ResponseEntity.ok(resource);
    }

    // Модель для информации об API
    public static class ApiInfo {
        private String title;
        private String description;
        private String version;
        private String termsOfService;
        private Contact contact;
        private String license;
        private String licenseUrl;
        private List<String> vendors;

        public ApiInfo(String title, String description, String version, String termsOfService,
                       Contact contact, String license, String licenseUrl, List<String> vendors) {
            this.title = title;
            this.description = description;
            this.version = version;
            this.termsOfService = termsOfService;
            this.contact = contact;
            this.license = license;
            this.licenseUrl = licenseUrl;
            this.vendors = vendors;
        }

// Конструкторы, геттеры и сеттеры
    }
}