package com.webdemo.backend.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.webdemo.backend.UserPrincipal;
import com.webdemo.backend.Reposity.UserReposity;
import com.webdemo.backend.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationInMs;

    @Autowired
    private UserReposity userRepository;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        logger.debug("Generating JWT token for user: {}", username);

        // Get user details from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get authorities as list
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = Jwts.builder()
                .setSubject(username)
                .claim("email", user.getEmail())
                .claim("roles", authorities)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        logger.debug("Generated JWT token for user: {} with authorities: {}", username, authorities);
        return token;
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            logger.debug("Extracted username from JWT: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from JWT: ", e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            logger.debug("JWT token is valid");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT token validation failed: ", e);
            return false;
        }
    }
    
    public String generateTokenFromUserPrincipal(UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);
        
        logger.debug("Generating JWT token for OAuth2 user: {}", user.getEmail());
        
        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("picture", user.getPicture())
                .claim("provider", user.getProvider().name())
                .claim("roles", authorities)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        
        logger.debug("Generated JWT token for OAuth2 user: {} with authorities: {}", user.getEmail(), authorities);
        return token;
    }
    
}
