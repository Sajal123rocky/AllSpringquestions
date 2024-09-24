package com.nw.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nw.model.TokenStore;

import java.io.IOException;

@Component
public class OneTimeTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if ("/update-password".equals(requestURI) || "/forget-password".equals(requestURI)) {
            String email = request.getParameter("email");
            String token = request.getParameter("resetToken");

            if (email == null || token == null || !tokenStore.isValid(email, token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

