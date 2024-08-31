package com.rin.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotNull(message = "INVALID_LOGIN_INFORMATION")
    @NotEmpty(message = "INVALID_LOGIN_INFORMATION")
    @NotBlank(message = "INVALID_LOGIN_INFORMATION")
    String username;
    @NotNull(message = "INVALID_LOGIN_INFORMATION")
    @NotEmpty(message = "INVALID_LOGIN_INFORMATION")
    @NotBlank(message = "INVALID_LOGIN_INFORMATION")
    String password;
}
