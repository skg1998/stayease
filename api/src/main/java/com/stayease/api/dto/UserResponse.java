package com.stayease.api.dto;

import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {
	private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;
    
    public static UserResponse convert(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .build();
    }
}