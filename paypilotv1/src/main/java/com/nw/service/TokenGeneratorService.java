package com.nw.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nw.model.Users;
import com.nw.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class TokenGeneratorService {
	
	@Autowired
	private UserRepository userRepository;
	
	
	private static final String SECRET="B91587B023B283C5A0C1D3326B3105322C49A0E35499E15DDD72876E746EBCD165A8CBE1E9635582D0B1073B1A9C139B9B39523D596DE6916F4960037BDB98CD";
	
	private static final long VALIDITY=TimeUnit.MINUTES.toMillis(1);
	
	private static final long VALIDITY1=TimeUnit.MINUTES.toMillis(15);
	
	public String generateOneTimeToken(String email) throws NoSuchAlgorithmException{
		
		Users user=userRepository.findByEmail(email).orElse(null);
		
		String passwordHash=user.getPassword();
		String dataToHash=email+":"+passwordHash+":"+LocalDateTime.now();
		
		MessageDigest digest=MessageDigest.getInstance("SHA-256");
		
		byte[] hashBytes=digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
		
		return Base64.getEncoder().encodeToString(hashBytes);
	}
	
	public String generateAccessToken(UserDetails userDetails) {
		
		System.out.println(userDetails.getUsername());
		Map<String, String> claims=new HashMap<>();
		claims.put("iss","rocky");
		return Jwts.builder()
		.claims(claims)
		.issuer("rocky")
		.subject(userDetails.getUsername())
		.issuedAt(Date.from(Instant.now()))
		.expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
		.signWith(generateKey())
		.compact();
		
	}
	
	public String generateRefereshToken(UserDetails userDetails) {
		
		Map<String, String> claims=new HashMap<>();
		claims.put("iss","rocky");
		return Jwts.builder()
		.issuer("rocky")
		.claim("scope","REFRESH_TOKEN")
		.subject(userDetails.getUsername())
		.issuedAt(Date.from(Instant.now()))
		.expiration(Date.from(Instant.now().plusMillis(VALIDITY1)))
		.signWith(generateKey())
		.compact();
		
	}
	
	private SecretKey generateKey() {
		byte[] decodedKey=Base64.getDecoder().decode(SECRET);
		return Keys.hmacShaKeyFor(decodedKey);
	}
	
	public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    public Claims getClaims(String jwt) {
        return Jwts.parser()
                 .verifyWith(generateKey())
                 .build()
                 .parseSignedClaims(jwt)
                 .getPayload();
    }

    public boolean isTokenValid(String jwt,boolean isRefreshToken) {
        Claims claims = getClaims(jwt);
        if (isRefreshToken) {
            return claims.get("scope", String.class).equals("REFRESH_TOKEN")
                    && claims.getExpiration().after(Date.from(Instant.now()));
        } else {
            return claims.get("scope") == null // access token doesn't have the "scope" claim
                    && claims.getExpiration().after(Date.from(Instant.now()));
        }
    }
    
    public boolean isTokenValid(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
	
}
