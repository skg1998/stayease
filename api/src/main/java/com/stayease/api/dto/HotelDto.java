package com.stayease.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {
	@NotEmpty(message = "Hotel name is required.")
	private String hotelName;
	
	@NotEmpty(message = "Hotel location is required.")
	private String location;
	
	@NotEmpty(message = "Hotel description is required.")
	private String description;
	
	@NotNull(message = "Available room is required.")
    @Positive(message = "Available room must be a positive number.")
	private Integer availableRoom;
}
