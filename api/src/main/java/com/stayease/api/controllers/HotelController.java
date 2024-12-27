package com.stayease.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stayease.api.dto.AppResponse;
import com.stayease.api.dto.HotelDto;
import com.stayease.api.dto.HotelResponse;
import com.stayease.api.dto.SuccessResponse;
import com.stayease.api.services.HotelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hotel")
@RequiredArgsConstructor
public class HotelController {
	private final HotelService hotelService;
	
	@PostMapping("/register")
    public ResponseEntity<AppResponse> registerHotel(@Valid @RequestBody HotelDto hotelDto) {
		hotelService.registerHotel(hotelDto);
        return responseMaker("register hotel successfully", HttpStatus.OK, null);
    }
	
	@PutMapping("/update/{hotelId}")
    public ResponseEntity<AppResponse> updateHotel(@PathVariable("hotelId") Long hotelId, @Valid @RequestBody HotelDto hotelDto) {
		hotelService.updateHotel(hotelId, hotelDto);
        return responseMaker("update hotel details successfully", HttpStatus.OK, null);
    }
	
	@GetMapping("/{hotelId}")
    public ResponseEntity<AppResponse> hotelById(@PathVariable("hotelId") Long hotelId) {
		HotelResponse hotelResponse = hotelService.findHotelById(hotelId);
        return responseMaker("fetch hotel detail successfully", HttpStatus.OK, hotelResponse);
    }
	
	@GetMapping("/")
    public ResponseEntity<AppResponse> hotels() {
		List<HotelResponse> hotelResponses = hotelService.getHotels();
        return responseMaker("fetch all hotels detail successfully", HttpStatus.OK, hotelResponses);
    }
	
	@DeleteMapping("/{hotelId}")
    public ResponseEntity<AppResponse> deleteUser(@PathVariable("hotelId") Long hotelId) {
		hotelService.deleteHotel(hotelId);
        return responseMaker("delete hotel successfully", HttpStatus.OK, null);
    }
    
    private <T> ResponseEntity<AppResponse> responseMaker(String message, HttpStatus httpStatus, T data){
		SuccessResponse<T> successResponse = new SuccessResponse<>(message, data);
        AppResponse response = new AppResponse(true, successResponse, null);
        return response.toResponseEntity(httpStatus);
	}
}
