package com.nw.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nw.model.RefreshToken;
import com.nw.repository.RefreshTokenRepo;
import com.nw.service.JwtService;
import com.nw.service.TokenGeneratorService;
import com.nw.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtRefreshTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RefreshTokenRepo refreshTokenRepo;
	
	@Autowired
	private TokenGeneratorService tokenGeneratorService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		/*if("/refresh-token".equals(requestURI)) {*/
		String authHeader=request.getHeader("Authorization");
		if(authHeader==null||!authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String jwt=authHeader.substring(7);
		String username=tokenGeneratorService.extractUsername(jwt);
		if(username!=null&&SecurityContextHolder.getContext().getAuthentication()==null) {
			System.out.println("Inside refresh token filter 3");
			RefreshToken isRefreshTokenValid=refreshTokenRepo.findByRefreshToken(jwt);
			boolean validity=isRefreshTokenValid.isRevoked();
			UserDetails userDetails=userService.loadUserByUsername(username);
			Claims claims = tokenGeneratorService.getClaims(jwt);
			boolean check=claims.get("scope", String.class).equals("REFRESH_TOKEN");
			System.out.println("Value of validity is "+validity);
			if(userDetails!=null&&tokenGeneratorService.isTokenValid(jwt)&&!validity&&check) {
				UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
						username, 
						userDetails.getPassword(),
						userDetails.getAuthorities()
						);
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		/*}*/
		filterChain.doFilter(request, response);
		
	}
	
}
