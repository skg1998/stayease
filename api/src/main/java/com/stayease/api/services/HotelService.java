package com.stayease.api.services;

import java.util.List;

import com.stayease.api.dto.HotelDto;
import com.stayease.api.dto.HotelResponse;

public interface HotelService {
	public void registerHotel(HotelDto hotelDto);
	public HotelResponse getHotel(Long hotelId);
	public List<HotelResponse> getHotels();
	public void deleteHotel(Long hotelId);
	public void updateHotel(Long hotelId, HotelDto hotelDto);
}
