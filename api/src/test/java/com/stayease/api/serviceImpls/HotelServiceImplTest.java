package com.stayease.api.serviceImpls;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stayease.api.dto.HotelDto;
import com.stayease.api.dto.HotelResponse;
import com.stayease.api.entity.Hotel;
import com.stayease.api.entity.User;
import com.stayease.api.enumeration.Role;
import com.stayease.api.exceptions.BadRequestException;
import com.stayease.api.repository.HotelRepository;
import com.stayease.api.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class HotelServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private User adminUser;
    private User managerUser;
    private Hotel mockHotel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setRole(Role.ADMIN);

        managerUser = new User();
        managerUser.setRole(Role.HOTELMANAGER);

        mockHotel = new Hotel();
        mockHotel.setId(1L);
        mockHotel.setHotelName("Test Hotel");
        mockHotel.setDescription("Description");
        mockHotel.setLocation("Location");
        mockHotel.setAvailableRoom(10);
    }

    @Test
    void testRegisterHotel_AsAdmin() {
        HotelDto hotelDto = new HotelDto("Test Hotel", "Description", "Location", 10);

        when(userService.getUser()).thenReturn(adminUser);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);

        hotelService.registerHotel(hotelDto);

        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testRegisterHotel_AsNonAdmin() {
        User user = new User();
        user.setRole(Role.CUSTOMER);

        HotelDto hotelDto = new HotelDto("Test Hotel", "Description", "Location", 10);

        when(userService.getUser()).thenReturn(user);

        assertThrows(BadRequestException.class, () -> hotelService.registerHotel(hotelDto));

        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void testFindHotelById() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));

        HotelResponse response = hotelService.findHotelById(1L);

        assertNotNull(response);
        assertEquals(mockHotel.getHotelName(), response.getHotelName());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));

        Hotel hotel = hotelService.getHotel(1L);

        assertNotNull(hotel);
        assertEquals(mockHotel.getId(), hotel.getId());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHotels() {
        List<Hotel> hotels = Arrays.asList(mockHotel);
        when(hotelRepository.findAll()).thenReturn(hotels);

        List<HotelResponse> responses = hotelService.getHotels();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(mockHotel.getHotelName(), responses.get(0).getHotelName());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void testDeleteHotel_AsAdmin() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));
        when(userService.getUser()).thenReturn(adminUser);

        hotelService.deleteHotel(1L);

        verify(hotelRepository, times(1)).delete(mockHotel);
    }

    @Test
    void testDeleteHotel_AsNonAdmin() {
        User user = new User();
        user.setRole(Role.CUSTOMER);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));
        when(userService.getUser()).thenReturn(user);

        assertThrows(BadRequestException.class, () -> hotelService.deleteHotel(1L));

        verify(hotelRepository, never()).delete(mockHotel);
    }

    @Test
    void testUpdateHotel_AsManager() {
        HotelDto hotelDto = new HotelDto("Updated Hotel", "Updated Location", "Updated Description", 5);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));
        when(userService.getUser()).thenReturn(managerUser);

        hotelService.updateHotel(1L, hotelDto);

        assertEquals("Updated Hotel", mockHotel.getHotelName());
        assertEquals("Updated Description", mockHotel.getDescription());
        assertEquals("Updated Location", mockHotel.getLocation());
        assertEquals(5, mockHotel.getAvailableRoom());
        verify(hotelRepository, times(1)).save(mockHotel);
    }

    @Test
    void testUpdateHotel_AsNonManager() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        HotelDto hotelDto = new HotelDto("Updated Hotel", "Updated Location", "Updated Description", 5);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(mockHotel));
        when(userService.getUser()).thenReturn(user);

        assertThrows(BadRequestException.class, () -> hotelService.updateHotel(1L, hotelDto));

        verify(hotelRepository, never()).save(mockHotel);
    }
}
