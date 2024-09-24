package com.nw.service;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nw.model.RefreshToken;
import com.nw.model.Users;
import com.nw.repository.RefreshTokenRepo;
import com.nw.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class JwtService {
	
//	private static final String SECRET="B91587B023B283C5A0C1D3326B3105322C49A0E35499E15DDD72876E746EBCD165A8CBE1E9635582D0B1073B1A9C139B9B39523D596DE6916F4960037BDB98CD";
//	
//	private static final long VALIDITY=TimeUnit.MINUTES.toMillis(1);
//	
//	private static final long VALIDITY1=TimeUnit.MINUTES.toMillis(2);
	
//	UserDetails userDetails;
//	HttpServletResponse response;
//	String refToken;
	
	@Autowired
	RefreshTokenRepo refreshTokenRepo;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private  UserService userService;
	
	@Autowired
	private TokenGeneratorService tokenGeneratorService;
	
//	@Autowired
//    private AuthenticationManager authenticationManager;
	
	 @Autowired
	 private UserService myUserDetailService;
	 
	 public String getJwtTokens(UserDetails userDetails,HttpServletResponse response) {
		 
		 String accessToken=tokenGeneratorService.generateAccessToken(userDetails);
		 String refreshToken=tokenGeneratorService.generateRefereshToken(userDetails);
		 saveRefreshToken(userDetails, refreshToken);
		 createRefreshTokenCookie(response, refreshToken);
		 String result="Access Token is "+accessToken+" Refresh token is "+refreshToken;
		 return result;
		 
	 }
	
//	public String generateToken(UserDetails userDetails,HttpServletResponse response) {
//		this.userDetails=userDetails;
//		this.response=response;
//		System.out.println(userDetails.getUsername());
//		Map<String, String> claims=new HashMap<>();
//		claims.put("iss","rocky");
//		refToken=generateRefereshToken();
//		createRefreshTokenCookie(response, refToken);
//		return Jwts.builder()
//		.claims(claims)
//		.subject(userDetails.getUsername())
//		.issuedAt(Date.from(Instant.now()))
//		.expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
//		.signWith(generateKey())
//		.compact();
//		
//		
//	}
//	
//	public String generateTokenAfterRefresh(UserDetails userDetails) {
//		Map<String, String> claims=new HashMap<>();
//		claims.put("iss","rocky");
//		return Jwts.builder()
//				.claims(claims)
//				.subject(userDetails.getUsername())
//				.issuedAt(Date.from(Instant.now()))
//				.expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
//				.signWith(generateKey())
//				.compact();
//	}
//	
//	public String generateRefereshToken() {
//		Map<String, String> claims=new HashMap<>();
//		claims.put("iss","rocky");
//		String token=Jwts.builder()
//		.claim("scope","REFRESH_TOKEN")
//		.subject(userDetails.getUsername())
//		.issuedAt(Date.from(Instant.now()))
//		.expiration(Date.from(Instant.now().plusMillis(VALIDITY1)))
//		.signWith(generateKey())
//		.compact();
//		saveRefreshToken(userDetails,token);
//		return token;
//	}
	
	@Transactional
	private void saveRefreshToken(UserDetails userDetails,String token) {
		String u=userDetails.getUsername();
		Users u1;
		if(u.contains("@")) {
			u1=userRepository.findByEmail(u).orElse(null);
		}
		else
			u1=userRepository.findById(u).orElse(null);
		
		RefreshToken ref=new RefreshToken();
		ref.setRefreshToken(token);
		ref.setRevoked(false);
		ref.setUserInfo(u1);
		
		refreshTokenRepo.save(ref);
	}
	
	
	
	public Cookie createRefreshTokenCookie(HttpServletResponse response,String refTokString) {
		Cookie refreshTokenCookie=new Cookie("refresh_token",refTokString);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true);
		refreshTokenCookie.setPath("/verify-otp");
		refreshTokenCookie.setMaxAge(15*60);
		response.addCookie(refreshTokenCookie);
		return refreshTokenCookie;
	}
	
//	private SecretKey generateKey() {
//		byte[] decodedKey=Base64.getDecoder().decode(SECRET);
//		return Keys.hmacShaKeyFor(decodedKey);
//	}
	
	
    
    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
    	
    	
//    	if(!authorizationHeader.startsWith("Bearer ")){
//            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify your token type");
//        }
//    	
//    	final String rT=authorizationHeader.substring(7);
//    	
//    	RefreshToken refreshToken=refreshTokenRepo.findByRefreshToken(rT);
//    	
//    	if(refreshToken.isRevoked())
//    		return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked");
//    		
//    		Users u2=refreshToken.getUserInfo();
//    		
//    		Authentication authentication = createAuthenticationObject(u2);
//    		System.out.println("hello from fun1");
//    		if(authentication.isAuthenticated()) {
//    		
//    		String token=tokenGeneratorService.generateAccessToken(myUserDetailService.loadUserByUsername(u2.getUserId()));
//    	//(myUserDetailService.loadUserByUsername(u2.getUserId()));
//    		System.out.println("Generated token is "+token);
//    		return ResponseEntity.ok(token);
//    		}
//    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    	
    	final String rT=authorizationHeader.substring(7);
    	Claims claims = tokenGeneratorService.getClaims(rT);
		boolean check=claims.get("scope", String.class).equals("REFRESH_TOKEN");
		RefreshToken refreshToken=refreshTokenRepo.findByRefreshToken(rT);
		boolean validity=refreshToken.isRevoked();
            if (tokenGeneratorService.isTokenValid(rT)&&check&&!validity) {
                String username = tokenGeneratorService.extractUsername(rT);
                UserDetails userDetails = userService.loadUserByUsername(username); // Load user details from your UserDetailsService

                return tokenGeneratorService.generateAccessToken(userDetails);
            } else {
                throw new RuntimeException("Invalid or expired refresh token");
            }
        
    		
    }
    
//    private static Authentication createAuthenticationObject(Users u3) {
//    	String userId=u3.getUserId();
//    	String password=u3.getPassword();
//    	System.out.println("userId:"+userId+" password "+password);
//    	UserDetails userDetails=userService.loadUserByUsername(userId);
//    	UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                userId, 
//                userDetails.getPassword(),
//                userDetails.getAuthorities()
//        );
//        
//        
//    	return authenticationToken;
//    }
}
