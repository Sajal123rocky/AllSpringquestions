package com.nw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nw.model.Users;
import com.nw.repository.UserRepository;

@RestController
public class RegistrationController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/register/user")
	public Users createUser(@RequestBody Users users) {
		
		Users u1=userRepository.findById(users.getUserId()).orElse(null);
		if(u1==null) {
		users.setPassword(passwordEncoder.encode(users.getPassword()));
		return userRepository.save(users);
		}
		throw new RuntimeException();
	}
	
}
