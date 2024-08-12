package com.rin.notification.controller;

import com.rin.event.dto.NotificationEvent;
import com.rin.notification.dto.request.Recipient;
import com.rin.notification.dto.request.SendEmailRequest;
import com.rin.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    EmailService emailService;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent request) {
        log.info("Message received: {}", request);
        emailService.sendEmail(SendEmailRequest.builder()
                        .to(Recipient.builder()
                                .email(request.getRecipient())
                                .build())
                        .subject(request.getSubject())
                        .htmlContent(request.getBody())
                .build());
    }
}
