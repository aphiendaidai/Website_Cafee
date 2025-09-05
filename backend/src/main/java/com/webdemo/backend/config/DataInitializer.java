//package com.webdemo.backend.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import com.webdemo.backend.Servicee.RoleService;
//import com.webdemo.backend.model.Role;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private RoleService roleService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Khởi tạo roles nếu chưa có
//        initializeRoles();
//    }
//
//    private void initializeRoles() {
//        try {
//            // Tạo role USER
//            Role userRole = new Role();
//            userRole.setName("USER");
//            roleService.saveRole(userRole);
//            System.out.println("Role USER đã được tạo");
//
//            // Tạo role ADMIN
//            Role adminRole = new Role();
//            adminRole.setName("ADMIN");
//            roleService.saveRole(adminRole);
//            System.out.println("Role ADMIN đã được tạo");
//        } catch (Exception e) {
//            System.out.println("Roles đã tồn tại hoặc có lỗi: " + e.getMessage());
//        }
//    }
//}
