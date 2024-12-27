package com.stayease.api.dto;


import com.stayease.api.entity.Hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class HotelResponse {
	private final Long id;
	private final String hotelName;
	private final String location;
	private final String description;
	private final Integer availableRoom;
	
	public static HotelResponse convert(Hotel hotel) {
		return HotelResponse.builder()
				.id(hotel.getId())
				.hotelName(hotel.getHotelName())
				.description(hotel.getDescription())
				.availableRoom(hotel.getAvailableRoom())
				.build();
	}
}
