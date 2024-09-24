package com.nw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nw.model.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
	
	RefreshToken findByRefreshToken(String refreshToken);
}
