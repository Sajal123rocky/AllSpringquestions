package com.nw.security;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nw.service.UserService;

import jakarta.mail.internet.MimeMessage;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private OneTimeTokenFilter oneTimeTokenFilter;
	
	@Autowired
	private JwtRefreshTokenFilter jwtRefreshTokenFilter;
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		return httpSecurity
		.csrf(AbstractHttpConfigurer::disable)
		.authorizeHttpRequests(registry->{
			registry.requestMatchers("/register/**","/authenticate","/verify-otp","/reset-password","/update-password","/forget-password","/refresh-token","/logOff").permitAll();
			registry.anyRequest().authenticated();
		})
		.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(oneTimeTokenFilter, UsernamePasswordAuthenticationFilter.class)
		.build();
	}
	
//	@Bean
//	public SecurityFilterChain anyFilterChain(HttpSecurity httpSecurity) throws Exception{
//		return httpSecurity
//		.csrf(AbstractHttpConfigurer::disable)
//		.authorizeHttpRequests(registry->{
//			registry.requestMatchers("/api/**").permitAll();
//			registry.anyRequest().authenticated();
//		})
//		.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//		.build();
//	}
	
//	@Order(2)
//	@Bean
//    public SecurityFilterChain anyFilterChain(HttpSecurity httpSecurity) throws Exception{
//        return httpSecurity
//                .securityMatcher(new AntPathRequestMatcher("/api/**"))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
	
	
	@Bean
    public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/refresh-token/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
//	
//	@Bean
//	public SecurityFilterChain resetFilterChain(HttpSecurity httpSecurity) throws Exception{
//		return httpSecurity
//		.csrf(AbstractHttpConfigurer::disable)
//		.authorizeHttpRequests(registry->{
//			registry.requestMatchers("/update-password").permitAll();
//			//registry.anyRequest().authenticated();
//		})
//		.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
//		.addFilterBefore(oneTimeTokenFilter, UsernamePasswordAuthenticationFilter.class)
//		
//		.build();
//	}
	
	@Bean
	 public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
	        return httpSecurity
	                .securityMatcher(new AntPathRequestMatcher("/logOff/**"))
	                .csrf(AbstractHttpConfigurer::disable)
	                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
	                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
	                .build();
	         }
	
	
	@Bean
	public UserDetailsService userDetailsService() {
		return userService;
	}
	
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(authenticationProvider());
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
