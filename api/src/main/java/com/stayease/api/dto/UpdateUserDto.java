package com.stayease.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
	@NotNull(message = "First name can not be null.")
	 private String firstName;

	@NotNull(message = "Last name can not be null.")
	 private String lastName;
}