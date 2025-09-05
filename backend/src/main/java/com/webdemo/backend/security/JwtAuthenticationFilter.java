package com.webdemo.backend.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webdemo.backend.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Bỏ qua OAuth2 callback và authorize endpoints
        if (path.startsWith("/login/oauth2/") || path.startsWith("/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getJWTfromRequest(request);
            logger.debug("Request to: {} - Token: {}", request.getRequestURI(), token != null ? "Present" : "Not present");

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromJWT(token);
                logger.debug("Valid token for user: {}", username);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.debug("Invalid or missing token for request: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("Error processing JWT token: ", e);
        }

        filterChain.doFilter(request, response);
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                  HttpServletResponse response,
//                                  FilterChain filterChain) throws ServletException, IOException {
//        try {
//            // get JWT (token) from http request
//            String token = getJWTfromRequest(request);
//            logger.debug("Request to: {} - Token: {}", request.getRequestURI(), token != null ? "Present" : "Not present");
//
//            // validate token
//            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
//                // get username from token
//                String username = tokenProvider.getUsernameFromJWT(token);
//                logger.debug("Valid token for user: {}", username);
//
//                // load user associated with token
//                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//                logger.debug("Loaded user details for: {}", username);
//
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // set spring security
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                logger.debug("Set authentication for user: {}", username);
//            } else {
//                logger.debug("Invalid or missing token for request: {}", request.getRequestURI());
//            }
//        } catch (Exception e) {
//            logger.error("Error processing JWT token: ", e);
//        }
//
//        filterChain.doFilter(request, response);
//    }

    // Bearer <accessToken>
    private String getJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
