package com.stayease.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stayease.api.dto.AppResponse;
import com.stayease.api.dto.BookingResponse;
import com.stayease.api.dto.SuccessResponse;
import com.stayease.api.services.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;
	
	@PostMapping("/hotel/{hotelId}")
    public ResponseEntity<AppResponse> bookingHotel(@Valid @PathVariable("hotelId") Long hotelId) {
		bookingService.bookingRoom(hotelId);
        return responseMaker("booking hotel successfully", HttpStatus.OK, null);
    }
	
	@GetMapping("/{bookingId}")
    public ResponseEntity<AppResponse> bookingDetailById(@PathVariable("bookingId") Long bookingId) {
		BookingResponse bookingResponse = bookingService.getBooking(bookingId);
        return responseMaker("fetch booking detail successfully", HttpStatus.OK, bookingResponse);
    }
	
	@GetMapping("/user")
    public ResponseEntity<AppResponse> allBookingOfUser() {
		List<BookingResponse> bookingResponses = bookingService.getBookingsForUser();
        return responseMaker("fetch bookings history for user successfully", HttpStatus.OK, bookingResponses);
    }
	
	@GetMapping("/")
    public ResponseEntity<AppResponse> hotels() {
		List<BookingResponse> bookingResponses = bookingService.getBookings();
        return responseMaker("fetch bookings history successfully", HttpStatus.OK, bookingResponses);
    }
	
	@DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<AppResponse> cancelBooking(@PathVariable("bookingId") Long bookingId) {
		bookingService.cancelBooking(bookingId);
        return responseMaker("cancel booking successfully", HttpStatus.OK, null);
    }
    
    private <T> ResponseEntity<AppResponse> responseMaker(String message, HttpStatus httpStatus, T data){
		SuccessResponse<T> successResponse = new SuccessResponse<>(message, data);
        AppResponse response = new AppResponse(true, successResponse, null);
        return response.toResponseEntity(httpStatus);
	}
}
