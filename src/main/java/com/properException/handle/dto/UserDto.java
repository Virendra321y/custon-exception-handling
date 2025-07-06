package com.properException.handle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a well‑formed email address")
    private String email;

    @NotBlank(message = "Contact number is required")
    // e.g. only digits, 10–15 chars
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact must be 10–15 digits")
    private String contact;
}

