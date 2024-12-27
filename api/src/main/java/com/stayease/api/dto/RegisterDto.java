package com.stayease.api.dto;

import com.stayease.api.enumeration.Role;

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
public class RegisterDto {
	@NotEmpty(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;

    private String firstName;

    private String lastName;
    
	private Role role;
	
	public Role getRole() {
        return role == null ? Role.CUSTOMER : role;
    }
}