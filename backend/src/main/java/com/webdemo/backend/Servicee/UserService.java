package com.webdemo.backend.Servicee;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webdemo.backend.Reposity.UserReposity;
import com.webdemo.backend.model.AuthProvider;
import com.webdemo.backend.model.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserReposity userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleService roleService;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    public User registerNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        user.addRole(roleService.getUserRole());
        
        User savedUser = userRepository.save(user);
        

        return savedUser;
    }
    
    public User registerNewAdmin(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Thêm role ADMIN
        user.addRole(roleService.getAdminRole());
        
        User savedUser = userRepository.save(user);

        
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    
    
    public User registerGoogleUser(String email, String name, String picture, String googleId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getProvider() == AuthProvider.LOCAL) {
                throw new RuntimeException("Email already registered with username/password. Please login normally.");
            }
            user.setName(name);
            user.setPicture(picture);
            return userRepository.save(user);
        }
        
        // Tạo user mới cho Google OAuth2
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setPicture(picture);
        newUser.setGoogleId(googleId);
        newUser.setProvider(AuthProvider.GOOGLE);
        newUser.setName(email); // Dùng email làm username
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // tránh null

        // Thêm role user mặc định
        newUser.addRole(roleService.getUserRole());
        
        return userRepository.save(newUser);
    }
    
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
    
    public Optional<User> findByEmailAndProvider(String email, AuthProvider provider) {
        return userRepository.findByEmailAndProvider(email, provider);
    }
    
}

