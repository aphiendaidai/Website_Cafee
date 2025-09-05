//package com.webdemo.backend.Control;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.webdemo.backend.Servicee.UserService;
//
//@RestController
//@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/dashboard")
//    public ResponseEntity<?> getAdminDashboard() {
//        return ResponseEntity.ok("Admin Dashboard - Chỉ ADMIN mới có thể truy cập!");
//    }
//
//    @GetMapping("/users")
//    public ResponseEntity<?> getAllUsers() {
//        return ResponseEntity.ok(userService.findAll());
//    }
//}
