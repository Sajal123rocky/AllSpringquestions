package com.nw.controller;



import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nw.model.OtpDetails;
import com.nw.model.Users;
import com.nw.repository.UserRepository;
import com.nw.service.EmailService;
import com.nw.service.JwtService;
import com.nw.service.LogoutHandlerService;
import com.nw.service.TokenGeneratorService;
import com.nw.service.UserService;
import com.nw.webtoken.LoginForm;

import jakarta.servlet.http.HttpServletResponse;




@RestController
public class LoginController {
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService myUserDetailService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenGeneratorService tokenGeneratorService;
    
    @Autowired
    private LogoutHandlerService logoutHandlerService;
    
    private Map<String, OtpDetails> otpStore = new ConcurrentHashMap<>();
    
	
	
	@PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()
        ));
        if (authentication.isAuthenticated()) {
        	String h=loginForm.username();
        	String email;
        	System.out.println(h);
        	if(!h.contains("@"))
        	{
        		Users u=userRepository.findById(h).orElse(null);
        		email=u.getEmail();
        	}
        	else
        	email=h;
        	System.out.println(email);
        	String emailResponse=emailService.sendOTP(email);
        	otpStore.put(email, new OtpDetails(emailResponse, LocalDateTime.now()));
        	return "Otp Generated and send to the registered email id";
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }
	
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email,
	                                        @RequestParam String otp,HttpServletResponse response) {
	    OtpDetails otpDetails = otpStore.get(email);

	    if (otpDetails == null) {
	    	return "not otp";
	    }

	    // Check if the OTP is expired (5 minutes expiration example)
	    if (otpDetails.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(5))) {
	    	return "otp expired";
	    }

	    if (otpDetails.getOtp().equals(otp)) {
	        otpStore.remove(email); // Clear the OTP after successful verification
	      String res= jwtService.getJwtTokens(myUserDetailService.loadUserByUsername(email),response);
	      return res;
	    } else {
	        return "Invalid Otp";
	    }
	}
	
	@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
	@PostMapping("/refresh-token")
	public ResponseEntity<?> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
		return ResponseEntity.ok(jwtService.getAccessTokenUsingRefreshToken(authorizationHeader));
	}
	

	@PostMapping("/logOff")
	public String getUserLogout(@RequestParam String refreshToken) {
		return logoutHandlerService.getLogout(refreshToken);
	}
	
	

}
