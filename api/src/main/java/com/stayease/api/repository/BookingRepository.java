package com.stayease.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.api.entity.Booking;
import com.stayease.api.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{
	List<Booking> findByCustomer(User user);
}
