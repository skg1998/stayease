package com.stayease.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.api.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String email);

}
