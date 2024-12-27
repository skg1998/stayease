package com.stayease.api.dto;

import java.time.LocalDateTime;

import com.stayease.api.entity.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
	private Long hotelId;
	private String hotelName;
	private String hotelDescription;
	private Long userId;
	private String userName;
	private String email;
	private Boolean bookingIsActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public static BookingResponse convert(Booking booking) {
		return BookingResponse.builder()
				.hotelId(booking.getHotel().getId())
				.hotelName(booking.getHotel().getHotelName())
				.hotelDescription(booking.getHotel().getDescription())
				.userId(booking.getCustomer().getId())
				.userName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName())
				.email(booking.getCustomer().getEmail())
				.bookingIsActive(booking.getIsActive())
				.createdAt(booking.getCreatedAt())
				.updatedAt(booking.getUpdatedAt())
				.build();
	}
}
