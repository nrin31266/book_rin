package com.devteria.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EmailRequest {
    Sender sender;
    List<Recipient> to;
    String htmlContent;
    String subject;
}
