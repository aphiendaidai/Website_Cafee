package com.webdemo.backend.Servicee;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webdemo.backend.Reposity.RoleReposity;
import com.webdemo.backend.model.Role;

import jakarta.annotation.PostConstruct;

@Service
public class RoleService {
    @Autowired
    private RoleReposity roleRepository;

//    @PostConstruct
//    public void init() {
//        if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
//            Role adminRole = new Role();
//            roleRepository.save(adminRole);
//        }
//        
//        if (!roleRepository.findByName("ROLE_USER").isPresent()) {
//            Role userRole = new Role();
//            roleRepository.save(userRole);
//        }
//    }
    // Comment out PostConstruct to avoid conflict with DataInitializer
     @PostConstruct
    public void init() {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
    }

    public Role getAdminRole() {
        return roleRepository.findByName("ADMIN").orElseThrow(() -> 
            new RuntimeException("Admin role not found"));
    }

    public Role getUserRole() {
        return roleRepository.findByName("USER").orElseThrow(() -> 
            new RuntimeException("User role not found"));
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}
