package com.stayease.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
	@NotEmpty(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;
}