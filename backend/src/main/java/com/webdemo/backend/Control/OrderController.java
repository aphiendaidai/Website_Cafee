package com.webdemo.backend.Control;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webdemo.backend.Servicee.OrderService;
import com.webdemo.backend.Servicee.UserService;
import com.webdemo.backend.model.Order;
import com.webdemo.backend.model.OrderRequestDTO;
import com.webdemo.backend.model.OrderResponseDTO;
import com.webdemo.backend.model.OrderStatus;
import com.webdemo.backend.model.User;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
//    @Autowired
//    private OrderResponseDTO orderResponseDTO; 

    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        try {
            logger.info("=== NHẬN REQUEST TẠO ORDER ===");
            logger.info("Request data: {}", orderRequest);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("User không được xác thực");
                return ResponseEntity.status(401).body("User không được xác thực");
            }

            String username = authentication.getName();
            logger.info("User đang tạo order: {}", username);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            logger.info("User ID: {}, Username: {}", user.getId(), user.getUsername());

            orderRequest.setUserId(user.getId());
            
            if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Danh sách món không được để trống");
            }
            
            if (orderRequest.getShippingAddress() == null || orderRequest.getShippingAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Địa chỉ giao hàng không được để trống");
            }
            
            if (orderRequest.getPhoneNumber() == null || orderRequest.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Số điện thoại không được để trống");
            }

            Order createdOrder = orderService.createOrder(orderRequest);
            logger.info("Tạo order thành công với ID: {}", createdOrder.getId());

            double totalPrice = createdOrder.getOrderItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("id", createdOrder.getId());
            response.put("status", createdOrder.getStatus().toString());
            response.put("orderDate", createdOrder.getOrderDate());
            response.put("shippingAddress", createdOrder.getShippingAddress());
            response.put("phoneNumber", createdOrder.getPhoneNumber());
            response.put("totalPrice", totalPrice); // ← Thông tin quan trọng nhất
            
            List<Map<String, Object>> itemDetails = createdOrder.getOrderItems().stream()
                    .map(item -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("menuItemName", item.getMenuItem().getName());
                        itemMap.put("quantity", item.getQuantity());
                        itemMap.put("pricePerItem", item.getPrice());
                        itemMap.put("subtotal", item.getPrice() * item.getQuantity());
                        return itemMap;
                    })
                    .collect(Collectors.toList());
            
            response.put("items", itemDetails);

            logger.info("Trả về response với tổng tiền: {}đ", totalPrice);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Lỗi khi tạo order: ", e);
            
            // Trả về thông tin lỗi chi tiết
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi tạo order");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


//    @GetMapping("/cart")
//    public ResponseEntity<?> getAllOrders() {
//        try {
//            List<Order> orders = orderService.getAllOrders();
//            return ResponseEntity.ok(orders);
//        } catch (Exception e) {
//            logger.error("Lỗi khi lấy danh sách orders: ", e);
//            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách orders: " + e.getMessage());
//        }
//    }
    

//    @GetMapping("/user")
//    public ResponseEntity<?> getOrdersByUser() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()) {
//                return ResponseEntity.status(401).body("User không được xác thực");
//            }
//
//            String username = authentication.getName();
//            User user = userService.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
//            
//            List<Order> orders = orderService.getOrdersByUser(user.getId());
//            return ResponseEntity.ok(orders);
//        } catch (Exception e) {
//            logger.error("Lỗi khi lấy orders của user: ", e);
//            return ResponseEntity.status(500).body("Lỗi khi lấy orders của user: " + e.getMessage());
//        }
//    }

//    @GetMapping("/user")
//    public ResponseEntity<?> getOrdersByUser() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated()) {
//                logger.warn("User không được xác thực");
//                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
//                        .body("User không được xác thực");
//            }
//
//            String username = authentication.getName();
//            logger.info("Lấy orders cho user: {}", username);
//
//            // Tìm user và lấy orders
//            User user = userService.findByUsername(username)
//                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
//
////            List<Order> orders = orderService.getOrdersByUser(user.getId());
//            
//            List<OrderItem> orderItems = orderService.getOrderItemsByUser(user.getId());
//            
//            logger.info("Tìm thấy {} orders", orderItems.size());
//
//            return ResponseEntity.ok(orderItems);
//            
//        } catch (RuntimeException e) {
//            logger.error("User không tồn tại: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
//                    .body("User không tồn tại: " + e.getMessage());
//                    
//        } catch (Exception e) {
//            logger.error("Lỗi khi lấy orders của user: ", e);
//            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
//                    .body("Lỗi hệ thống: " + e.getMessage());
//        }
//    }
    
    
    
    @GetMapping("/user")
    public ResponseEntity<?> getOrdersByUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("User không được xác thực");
                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
                        .body("User không được xác thực");
            }

            String username = authentication.getName();
            logger.info("Lấy orders cho user: {}", username);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            List<OrderResponseDTO> orderDTOs = orderService.getOrdersWithItemsByUser(user.getId());

            return ResponseEntity.ok(orderDTOs);

        } catch (RuntimeException e) {
            logger.error("User không tồn tại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body("User không tồn tại: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Lỗi khi lấy orders của user: ", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/admin/orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            // Kiểm tra admin role (bạn cần implement authorization)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
                    .body("Không có quyền truy cập");
            }
            
            List<OrderResponseDTO> orderDTOs = orderService.getAllOrdersWithItems();
            return ResponseEntity.ok(orderDTOs);
            
        } catch (Exception e) {
            logger.error("Lỗi khi lấy tất cả orders: ", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    
    
    
//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
//        try {
//            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
//            return ResponseEntity.ok(updatedOrder);
//        } catch (Exception e) {
//            logger.error("Lỗi khi cập nhật status order: ", e);
//            return ResponseEntity.status(500).body("Lỗi khi cập nhật status order: " + e.getMessage());
//        }
//    }
    
    @GetMapping("/admin/orders/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
                    .body("Không có quyền truy cập");
            }
            
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderResponseDTO> orderDTOs = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orderDTOs);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                .body("Status không hợp lệ: " + status);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy orders theo status: ", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
                    .body("Không có quyền truy cập");
            }
            
            String statusStr = request.get("status");
            OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            
            OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                .body("Status không hợp lệ");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi khi update order status: ", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    @PutMapping("/user/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Lấy user từ username
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            
            OrderResponseDTO cancelledOrder = orderService.cancelUserOrder(orderId, user.getId());
            return ResponseEntity.ok(cancelledOrder);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi khi huỷ order: ", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }
    
}
