package com.rin.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationPasswordRequest {
    @Size(min = 8, message = "PASSWORD_INVALID")
    @NotNull(message = "INVALID_INFORMATION")
    @NotEmpty(message = "INVALID_INFORMATION")
    @NotBlank(message = "INVALID_INFORMATION")
    String password;
}
