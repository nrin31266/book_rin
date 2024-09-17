package com.rin.identity.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.*;

import com.rin.identity.validator.DobConstraint;
import com.rin.identity.validator.TextConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotNull(message = "INVALID_INFORMATION")
    @NotEmpty(message = "INVALID_INFORMATION")
    @NotBlank(message = "INVALID_INFORMATION")
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;
    @NotNull(message = "INVALID_INFORMATION")
    @NotEmpty(message = "INVALID_INFORMATION")
    @NotBlank(message = "INVALID_INFORMATION")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    @TextConstraint(value = "First name", message = "NOT_EMPTY")
    String firstName;
    @TextConstraint(value = "Last name", message = "NOT_EMPTY")
    String lastName;
    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dob;
    @TextConstraint(value = "City", message = "NOT_EMPTY")
    String city;
    @TextConstraint(value = "Email", message = "NOT_EMPTY")
            @Email(message = "INVALID_EMAIL")
    String email;
    List<String> roles;
}
