package com.devteria.notification.Service;

import com.devteria.notification.dto.request.EmailRequest;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.dto.request.Sender;
import com.devteria.notification.dto.response.EmailResponse;
import com.devteria.notification.exception.AppException;
import com.devteria.notification.exception.ErrorCode;
import com.devteria.notification.respository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;
    String apiKey = "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";
    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Nguyen Van Rin")
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
