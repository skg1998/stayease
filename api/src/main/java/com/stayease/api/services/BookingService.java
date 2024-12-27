package com.stayease.api.services;

import java.util.List;

import com.stayease.api.dto.BookingResponse;

public interface BookingService {
	public void bookingRoom(Long hotelId);
	public void cancelBooking(Long bookingId);
	List<BookingResponse> getBookingsForUser();
	BookingResponse getBooking(Long bookingId);
	List<BookingResponse> getBookings();
}
