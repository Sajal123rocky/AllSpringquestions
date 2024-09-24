package com.nw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.nw.model.OtpDetails;
import com.nw.model.TokenStore;
import com.nw.model.Users;
import com.nw.repository.UserRepository;
import com.nw.service.EmailService;
import com.nw.service.TokenGeneratorService;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.nw.webtoken.LoginForm;

@RestController
public class PasswordResetController {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    private Map<String, OtpDetails> otpStore = new ConcurrentHashMap<>();
    
    
    @PostMapping("/forget-password")
	public String forgetPassword(@RequestBody LoginForm loginForm) {
		Users u1;
		String username=loginForm.username();
		if(username.contains("@")) {
			u1=userRepository.findByEmail(username).orElse(null);
		}
		else
			u1=userRepository.findById(username).orElse(null);
		
		if(u1==null)
			return "user does not exist";
		String email;
		if(!username.contains("@")) {
			Users u2=userRepository.findById(username).orElse(null);
			email=u2.getEmail();
		}
		else
			email=username;
		String otp=emailService.sendOTP(email);
		otpStore.put(email, new OtpDetails(otp, LocalDateTime.now()));
		return "Otp generated successfully";
		
	}
    
    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp) throws NoSuchAlgorithmException {
        OtpDetails otpDetails = otpStore.get(email);
        if (otpDetails == null) {
            return "no otp";
        }

        // Check if the OTP is expired (5 minutes expiration example)
        if (otpDetails.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(5))) {
            return "otp expired";
        }

        if (otpDetails.getOtp().equals(otp)) {
            otpStore.remove(email); // Clear the OTP after successful verification

            // Generate a one-time-use token with expiration time (e.g., 15 minutes)
            String resetToken = tokenGeneratorService.generateOneTimeToken(email); 
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);
            tokenStore.put(email, resetToken, expirationTime);

            return "Otp successfully validated. Use the token to reset your password: " + resetToken;
        } else {
            return "Invalid Otp";
        }
    }

    @PutMapping("/update-password")
    public String updatePassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String resetToken) {
//        if (!tokenStore.isValid(email, resetToken)) {
//            return "NOT AUTHORIZE";
//        }

        // Proceed with password update
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Invalidate the token after successful password reset
            tokenStore.remove(email);
            return "Password updated successfully";
        } else {
            return "User does not exist";
        }
    }

    
}

