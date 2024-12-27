package com.stayease.api.serviceImpls;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stayease.api.dto.HotelDto;
import com.stayease.api.dto.HotelResponse;
import com.stayease.api.entity.Hotel;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.BadRequestException;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.repository.HotelRepository;
import com.stayease.api.services.HotelService;
import com.stayease.api.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService{
	private final UserService userService;
	private final HotelRepository hotelRepository;
	
	@Override
	public void registerHotel(HotelDto hotelDto) {
		User user = userService.getUser();
		
		if(!user.getRole().name().equals(Role.ADMIN.name())) {
			throw new BadRequestException("only ADMIN has authrize to delete hotel");
		}
		
		Hotel hotel = new Hotel();
		hotel.setHotelName(hotelDto.getHotelName());
		hotel.setDescription(hotelDto.getDescription());
		hotel.setLocation(hotelDto.getLocation());
		hotel.setAvailableRoom(hotelDto.getAvailableRoom());
		
		hotelRepository.save(hotel);
		
	}

	@Override
	public HotelResponse getHotel(Long hotelId) {
		Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found for given hotel id"));
		return HotelResponse.convert(hotel);
	}

	@Override
	public List<HotelResponse> getHotels() {
		List<Hotel> hotels = hotelRepository.findAll();
		return hotels.stream().map(HotelResponse::convert).toList();
	}

	@Override
	public void deleteHotel(Long hotelId) {
		Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found for given hotel id"));
		User user = userService.getUser();
		
		if(!user.getRole().name().equals(Role.ADMIN.name())) {
			throw new BadRequestException("only ADMIN has authrize to delete hotel");
		}
		
		hotelRepository.delete(hotel);
	}

	@Override
	public void updateHotel(Long hotelId, HotelDto hotelDto) {
		Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found for given hotel id"));
		User user = userService.getUser();
		
		if(!user.getRole().name().equals(Role.HOTELMANAGER.name())) {
			throw new BadRequestException("only HOTELMANAGER has authrize to delete hotel");
		}
		
		hotel.setHotelName(hotelDto.getHotelName());
		hotel.setDescription(hotelDto.getDescription());
		hotel.setLocation(hotelDto.getLocation());
		hotel.setAvailableRoom(hotelDto.getAvailableRoom());
		
		hotelRepository.save(hotel);
	}

}
