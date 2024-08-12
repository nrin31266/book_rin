package com.rin.notification.service;

import com.rin.notification.dto.request.EmailRequest;
import com.rin.notification.dto.request.SendEmailRequest;
import com.rin.notification.dto.request.Sender;
import com.rin.notification.dto.response.EmailResponse;
import com.rin.notification.exception.AppException;
import com.rin.notification.exception.ErrorCode;
import com.rin.notification.respository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;
    @NonFinal
    @Value("${app.api-key}")
    String apiKey;
    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("NV Rin")
                        .email("nrin31266@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        }catch(FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
