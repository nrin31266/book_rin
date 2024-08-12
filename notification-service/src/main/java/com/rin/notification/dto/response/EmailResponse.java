    package com.rin.notification.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailResponse {
    String messageId;
}
