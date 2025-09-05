package com.webdemo.backend.Control;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webdemo.backend.UserPrincipal;
import com.webdemo.backend.Servicee.UserService;
import com.webdemo.backend.model.User;
import com.webdemo.backend.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class UserControll {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/login")
    public String loginPage() {
        return "Login endpoint";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "Register endpoint";
    }
    

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
		    new UsernamePasswordAuthenticationToken(
		        loginRequest.getUsername(), 
		        loginRequest.getPassword()
		    )
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// Generate JWT token
		String jwt = tokenProvider.generateToken(authentication);
		
		JwtAuthResponse response = new JwtAuthResponse();
		response.setAccessToken(jwt);
		response.setTokenType("Bearer");
		
		return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public Response register(@RequestBody User user) {
        try {
            userService.registerNewUser(user);
            return new Response(true, "Đăng ký thành công!");
        } catch (RuntimeException e) {
            return new Response(false, e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    public Response registerAdmin(@RequestBody User user) {
        try {
            userService.registerNewAdmin(user);
            return new Response(true, "Đăng ký Admin thành công!");
        } catch (RuntimeException e) {
            return new Response(false, e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new Response(true, "Đăng xuất thành công!"));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            return ResponseEntity.ok(userPrincipal.getUser());
        }
        return ResponseEntity.status(401).body(new Response(false, "User not authenticated"));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess() {
        return ResponseEntity.ok(new Response(true, "Google OAuth2 login successful"));
    }


    // Inner class đại diện cho phản hồi API
    public static class Response {
        private boolean success;
        private String message;

        public Response() {}

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Inner class cho JWT response
    public static class JwtAuthResponse {
        private String accessToken;
        private String tokenType;

        public JwtAuthResponse() {
            this.tokenType = "Bearer";
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }
}
