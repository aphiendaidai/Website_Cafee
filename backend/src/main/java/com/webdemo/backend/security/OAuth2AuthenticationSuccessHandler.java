package com.webdemo.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.webdemo.backend.UserPrincipal;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
//                                      Authentication authentication) throws IOException, ServletException {
//        String targetUrl = determineTargetUrl(request, response, authentication);
//
//        if (response.isCommitted()) {
//            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
//            return;
//        }
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, 
//                                       Authentication authentication) {
//        
//        // Tạo JWT token cho user đã đăng nhập Google
//        String token = jwtTokenProvider.generateTokenFromUserPrincipal((UserPrincipal) authentication.getPrincipal());
//        
//        // Redirect về frontend với token
//        String redirectUri = allowedOrigins[0];
//        
//        return UriComponentsBuilder.fromUriString(redirectUri + "/auth/oauth2/redirect")
//                .queryParam("token", token)
//                .build().toUriString();
//    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) {

        try {
            // ✅ Sinh JWT từ UserPrincipal
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtTokenProvider.generateTokenFromUserPrincipal(principal);

            // ✅ Lấy redirectUri từ allowedOrigins (chỉ cho phép domain trong config)
            String redirectUri = allowedOrigins.length > 0 ? allowedOrigins[0] : "http://localhost:3000";

            // ✅ Trả về frontend kèm token
            return UriComponentsBuilder.fromUriString(redirectUri + "/auth/oauth2/redirect")
                    .queryParam("token", token)
                    .build()
                    .toUriString();
        } catch (Exception e) {
            // Nếu có lỗi, redirect về frontend với thông báo lỗi
            String redirectUri = allowedOrigins.length > 0 ? allowedOrigins[0] : "http://localhost:3000";
            return UriComponentsBuilder.fromUriString(redirectUri + "/auth/oauth2/redirect")
                    .queryParam("error", "Authentication failed: " + e.getMessage())
                    .build()
                    .toUriString();
        }
    }
}

