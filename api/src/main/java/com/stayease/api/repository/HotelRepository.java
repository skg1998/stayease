package com.stayease.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.api.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long>{

}
