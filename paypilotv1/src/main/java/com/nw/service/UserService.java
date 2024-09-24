package com.nw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nw.model.Users;
import com.nw.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<Users> user;
		if(username.contains("@"))
		user=userRepository.findByEmail(username);
		else
			user=userRepository.findById(username);
		//Optional<Users> id=userRepository.findById();
		if(user.isPresent()) {
			var userObj=user.get();
			
			return User.builder()
			.username(userObj.getUserId())
			.password(userObj.getPassword())
			.build();
		}
		else {
			throw new UsernameNotFoundException(username);
		}
	}
}
