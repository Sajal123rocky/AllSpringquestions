package com.nw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.nw.model.RefreshToken;
import com.nw.repository.RefreshTokenRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LogoutHandlerService  {

	@Autowired
	private RefreshTokenRepo refreshTokenRepo;
	
	public String getLogout(String rT) {
		
		
		RefreshToken refreshToken=refreshTokenRepo.findByRefreshToken(rT);
		
		refreshToken.setRevoked(true);
		
		refreshTokenRepo.save(refreshToken);
		
		return "Log Out Success";
		
	}

}
