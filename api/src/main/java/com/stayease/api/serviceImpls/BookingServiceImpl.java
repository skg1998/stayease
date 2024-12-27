package com.stayease.api.serviceImpls;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stayease.api.dto.BookingResponse;
import com.stayease.api.entity.Booking;
import com.stayease.api.entity.Hotel;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.BadRequestException;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.repository.BookingRepository;
import com.stayease.api.repository.HotelRepository;
import com.stayease.api.services.BookingService;
import com.stayease.api.services.HotelService;
import com.stayease.api.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{
	private final UserService userService;
	private final HotelService hotelService;
	private final BookingRepository bookingRepository;
	private final HotelRepository hotelRepository;
	
	@Override
	@Transactional
	public void bookingRoom(Long hotelId){
		User user = userService.getUser();
		Hotel hotel = hotelService.getHotel(hotelId);
		
         if (hotel.getAvailableRoom() <= 0) {
            throw new IllegalStateException("No rooms available for booking.");
        }

        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setCustomer(user);
        booking.setIsActive(true);

        hotel.setAvailableRoom(hotel.getAvailableRoom() - 1);
        hotelRepository.save(hotel);

        bookingRepository.save(booking);
    }

	@Override
	@Transactional
    public void cancelBooking(Long bookingId) {
        User user = userService.getUser();
        
        if (!user.getRole().name().equals(Role.HOTELMANAGER.name())) {
            throw new SecurityException("Only managers can cancel bookings.");
        }
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for given id"));

        Hotel hotel = booking.getHotel();
        
        booking.setIsActive(false);
        bookingRepository.save(booking);

        hotel.setAvailableRoom(hotel.getAvailableRoom() + 1);
        hotelRepository.save(hotel);
    }
    
    @Override
    public List<BookingResponse> getBookingsForUser() {
    	User user = userService.getUser();
        List<Booking> bookings = bookingRepository.findByCustomer(user);
        return bookings.stream().map(BookingResponse::convert).toList();
    }
    
    @Override
    public BookingResponse getBooking(Long bookingId) {
    	User user = userService.getUser();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found for given id"));
        
        if(user.getId().equals(booking.getCustomer().getId())) {
        	throw new BadRequestException("your are not allowed to check other user booking details");
        }
        
        return BookingResponse.convert(booking);
    }
    
    @Override
    public List<BookingResponse> getBookings(){
    	List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(BookingResponse::convert).toList();
    }
}
