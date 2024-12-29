package com.stayease.api.serviceImpls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.stayease.api.dto.BookingResponse;
import com.stayease.api.entity.Booking;
import com.stayease.api.entity.Hotel;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.BadRequestException;
import com.stayease.api.repository.BookingRepository;
import com.stayease.api.repository.HotelRepository;
import com.stayease.api.services.HotelService;
import com.stayease.api.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    @Mock
    private HotelService hotelService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelRepository hotelRepository;

    private User mockUser;
    private Hotel mockHotel;
    private Booking mockBooking;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRole(Role.CUSTOMER);

        mockHotel = new Hotel();
        mockHotel.setId(1L);
        mockHotel.setAvailableRoom(5);

        mockBooking = new Booking();
        mockBooking.setId(1L);
        mockBooking.setHotel(mockHotel);
        mockBooking.setCustomer(mockUser);
        mockBooking.setIsActive(true);
    }

    @Test
    void testBookingRoom_success() {
        when(userService.getUser()).thenReturn(mockUser);
        when(hotelService.getHotel(1L)).thenReturn(mockHotel);
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        bookingService.bookingRoom(1L);

        Assertions.assertEquals(4, mockHotel.getAvailableRoom());
         verify(hotelRepository,  times(1)).save(mockHotel);
         verify(bookingRepository,  times(1)).save( any(Booking.class));
    }

    @Test
    void testBookingRoom_noRoomsAvailable() {
        mockHotel.setAvailableRoom(0);
        when(userService.getUser()).thenReturn(mockUser);
        when(hotelService.getHotel(1L)).thenReturn(mockHotel);

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> bookingService.bookingRoom(1L));

        Assertions.assertEquals("No rooms available for booking.", exception.getMessage());
    }

    @Test
    void testCancelBooking_success() {
        mockUser.setRole(Role.HOTELMANAGER);
        when(userService.getUser()).thenReturn(mockUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        bookingService.cancelBooking(1L);

        Assertions.assertFalse(mockBooking.getIsActive());
        Assertions.assertEquals(6, mockHotel.getAvailableRoom());
         verify(hotelRepository,  times(1)).save(mockHotel);
         verify(bookingRepository,  times(1)).save(mockBooking);
    }

    @Test
    void testCancelBooking_unauthorizedUser() {
        mockUser.setRole(Role.CUSTOMER);
        when(userService.getUser()).thenReturn(mockUser);

        SecurityException exception = Assertions.assertThrows(SecurityException.class, () -> bookingService.cancelBooking(1L));

        Assertions.assertEquals("Only managers can cancel bookings.", exception.getMessage());
    }

    @Test
    void testGetBookingsForUser_success() {
        when(userService.getUser()).thenReturn(mockUser);
        when(bookingRepository.findByCustomer(mockUser)).thenReturn(List.of(mockBooking));

        List<BookingResponse> responses = bookingService.getBookingsForUser();

        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(mockBooking.getId(), responses.get(0).getBookingId());
    }

    @Test
    void testGetBooking_success() {
        when(userService.getUser()).thenReturn(mockUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        BookingResponse response = bookingService.getBooking(1L);

        Assertions.assertEquals(mockBooking.getId(), response.getBookingId());
    }

    @Test
    void testGetBooking_unauthorizedAccess() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        mockBooking.setCustomer(anotherUser);

        when(userService.getUser()).thenReturn(mockUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class, () -> bookingService.getBooking(1L));

        Assertions.assertEquals("your are not allowed to check other user booking details", exception.getMessage());
    }

    @Test
    void testGetBookings_success() {
        when(bookingRepository.findAll()).thenReturn(List.of(mockBooking));

        List<BookingResponse> responses = bookingService.getBookings();

        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(mockBooking.getId(), responses.get(0).getBookingId());
    }
}
