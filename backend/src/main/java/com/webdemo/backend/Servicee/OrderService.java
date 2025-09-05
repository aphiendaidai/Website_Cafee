package com.webdemo.backend.Servicee;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webdemo.backend.Reposity.MenuItemReposity;
import com.webdemo.backend.Reposity.OrderItemRepository;
import com.webdemo.backend.Reposity.OrderRepository;
import com.webdemo.backend.Reposity.UserReposity;
import com.webdemo.backend.model.MenuItem;
import com.webdemo.backend.model.Order;
import com.webdemo.backend.model.OrderItem;
import com.webdemo.backend.model.OrderItemRequestDTO;
import com.webdemo.backend.model.OrderRequestDTO;
import com.webdemo.backend.model.OrderResponseDTO;
import com.webdemo.backend.model.OrderStatus;
import com.webdemo.backend.model.User;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository itemRepository;

    @Autowired
    private UserReposity userRepository;
    
    @Autowired
    private MenuItemReposity menuItemRepository;
    
    

//    @Transactional
    public Order createOrder(OrderRequestDTO dto) {
        System.out.println("=== BẮT ĐẦU TẠO ORDER ===");
        System.out.println("User ID: " + dto.getUserId());
        System.out.println("Địa chỉ: " + dto.getShippingAddress());
        System.out.println("SĐT: " + dto.getPhoneNumber());
        System.out.println("Số món: " + dto.getItems().size());
        
        // Lấy user từ database
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + dto.getUserId()));
        System.out.println("Tìm thấy user: " + user.getUsername());

        // Tạo Order mới
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(dto.getShippingAddress());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Lưu order trước để có ID
        Order savedOrder = orderRepository.save(order);
        System.out.println("Đã tạo order với ID: " + savedOrder.getId());

        // Danh sách order items và tổng tiền
        List<OrderItem> orderItems = new ArrayList<>();
        double totalOrderAmount = 0.0;
        
        // Xử lý từng món trong đơn hàng
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItemRequestDTO itemDTO = dto.getItems().get(i);
            System.out.println("\n--- XỬ LÝ MỤC " + (i + 1) + " ---");
            System.out.println("Menu Item ID: " + itemDTO.getMenuItemId());
            System.out.println("Số lượng: " + itemDTO.getQuantity());
            System.out.println("Size: " + itemDTO.getSize());
            
            // Validate dữ liệu đầu vào
            if (itemDTO.getMenuItemId() == null) {
                throw new RuntimeException("Menu Item ID không được để trống");
            }
            if (     itemDTO.getQuantity() <= 0) {
                 throw new RuntimeException("Số lượng phải lớn hơn 0");
            }
//            
            // Lấy thông tin món ăn từ database
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món với ID: " + itemDTO.getMenuItemId()));
            System.out.println("Tìm thấy món: " + menuItem.getName() + " - Giá gốc: " + menuItem.getPrice() + "đ");

            // Tính giá cho 1 món (bao gồm phí size)
            double basePrice = menuItem.getPrice();
            double sizeExtra = 0.0;
            String sizeText = "Small (không phụ thu)";
            
            if (itemDTO.getSize() != null && !itemDTO.getSize().trim().isEmpty()) {
                String size = itemDTO.getSize().toLowerCase().trim();
                switch (size) {
                    case "medium":
                        sizeExtra = 10000.0;
                        sizeText = "Medium (+10,000đ)";
                        break;
                    case "large":
                        sizeExtra = 16000.0;
                        sizeText = "Large (+16,000đ)";
                        break;
                    case "small":
                    default:
                        sizeExtra = 0.0;
                        sizeText = "Small (không phụ thu)";
                        break;
                }
            }
            
            double pricePerItem = basePrice + sizeExtra; // Giá 1 món đã bao gồm size
            double totalPriceForThisItem = pricePerItem * itemDTO.getQuantity(); // Tổng giá cho quantity này
            
            System.out.println("Size: " + sizeText);
            System.out.println("Giá 1 món (đã tính size): " + pricePerItem + "đ");
            System.out.println("Số lượng: " + itemDTO.getQuantity());
            System.out.println("Thành tiền: " + totalPriceForThisItem + "đ");

            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(totalPriceForThisItem); // Lưu giá 1 món (để dễ tính toán sau này)
            orderItem.setUserId(order.getUser().getId()); // ← SET userId khi tạo

            orderItems.add(orderItem);
            
            // Cộng dồn tổng tiền đơn hàng
            totalOrderAmount += totalPriceForThisItem;
        }

        System.out.println("\n=== TỔNG KẾT ===");
        System.out.println("Tổng số mục: " + orderItems.size());
        System.out.println("Tổng tiền đơn hàng: " + totalOrderAmount + "đ");

        // Lưu tất cả order items vào database
        List<OrderItem> savedOrderItems = itemRepository.saveAll(orderItems);
        System.out.println("Đã lưu " + savedOrderItems.size() + " order items");

        // Gán lại danh sách order items vào order để trả về
        savedOrder.setOrderItems(savedOrderItems);
        
        System.out.println("=== HOÀN THÀNH TẠO ORDER ===");
        System.out.println("Order ID: " + savedOrder.getId());
        System.out.println("Tổng tiền: " + totalOrderAmount + "đ");
        System.out.println("Trạng thái: " + savedOrder.getStatus());
        
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public List<OrderItem> getOrderItemsByUser(Long userId) {
        return itemRepository.findByUserIdOrderByIdDesc(userId);
    }
    


    public List<OrderResponseDTO> getOrdersWithItemsByUser(Long userId) {	
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {
        	OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getId());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setPhoneNumber(order.getPhoneNumber());
            dto.setStatus(order.getStatus().name());
            dto.setOrderDate(order.getOrderDate());
            dto.setCompletedDate(order.getCompletedDate());

            var itemDTOs = order.getOrderItems().stream()
                    .map(item -> new OrderResponseDTO.OrderItemDTO(
                            item.getMenuItem().getId(),
                            item.getMenuItem().getName(),
                            item.getMenuItem().getDescription(),
                            item.getMenuItem().getImageUrl(),
                            item.getMenuItem().getCategory(),
                            item.getMenuItem().getType(),
                            item.getQuantity(),
                            item.getPrice()
                    ))
                    .collect(Collectors.toList());

            dto.setItems(itemDTOs);
            return dto;
        }).collect(Collectors.toList());
    }
    
    
    public List<OrderResponseDTO> getAllOrdersWithItems() {
        List<Order> orders = orderRepository.findAll(); // hoặc findAllByOrderByOrderDateDesc() để sort theo ngày
        
        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getId());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setPhoneNumber(order.getPhoneNumber());
            dto.setStatus(order.getStatus().name());
            dto.setOrderDate(order.getOrderDate());
            dto.setCompletedDate(order.getCompletedDate());
            
            // Thêm thông tin user để admin biết order của ai
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
            
            var itemDTOs = order.getOrderItems().stream()
                .map(item -> new OrderResponseDTO.OrderItemDTO(
                    item.getMenuItem().getId(),
                    item.getMenuItem().getName(),
                    item.getMenuItem().getDescription(),
                    item.getMenuItem().getImageUrl(),
                    item.getMenuItem().getCategory(),
                    item.getMenuItem().getType(),
                    item.getQuantity(),
                    item.getPrice()
                ))
                .collect(Collectors.toList());
                
            dto.setItems(itemDTOs);
            return dto;
        }).collect(Collectors.toList());
    }
    

//    public Order updateOrderStatus(Long orderId, OrderStatus status) {	
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//        order.setStatus(status);
//        
//        // Nếu status là COMPLETED, set completedDate
//        if (status == OrderStatus.COMPLETED) {
//            order.setCompletedDate(LocalDateTime.now());
//        }
//        
//        return orderRepository.save(order);
//    }
    
    
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        
        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getId());
            dto.setShippingAddress(order.getShippingAddress());
            dto.setPhoneNumber(order.getPhoneNumber());
            dto.setStatus(order.getStatus().name());
            dto.setOrderDate(order.getOrderDate());
            dto.setCompletedDate(order.getCompletedDate());
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
            
            var itemDTOs = order.getOrderItems().stream()
                .map(item -> new OrderResponseDTO.OrderItemDTO(
                    item.getMenuItem().getId(),
                    item.getMenuItem().getName(),
                    item.getMenuItem().getDescription(),
                    item.getMenuItem().getImageUrl(),
                    item.getMenuItem().getCategory(),
                    item.getMenuItem().getType(),
                    item.getQuantity(),
                    item.getPrice()
                ))
                .collect(Collectors.toList());
                
            dto.setItems(itemDTOs);
            return dto;
        }).collect(Collectors.toList());
    }

    // 3. Thêm method để update status order (admin xác nhận, giao hàng)
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order không tồn tại"));
        
        order.setStatus(newStatus);
        
        // Nếu status là COMPLETED thì set completedDate
        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedDate(LocalDateTime.now());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Convert to DTO và return
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(savedOrder.getId());
        dto.setShippingAddress(savedOrder.getShippingAddress());
        dto.setPhoneNumber(savedOrder.getPhoneNumber());
        dto.setStatus(savedOrder.getStatus().name());
        dto.setOrderDate(savedOrder.getOrderDate());
        dto.setCompletedDate(savedOrder.getCompletedDate());
        dto.setUserId(savedOrder.getUser().getId());
        dto.setUsername(savedOrder.getUser().getUsername());
        
        var itemDTOs = savedOrder.getOrderItems().stream()
            .map(item -> new OrderResponseDTO.OrderItemDTO(
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getMenuItem().getDescription(),
                item.getMenuItem().getImageUrl(),
                item.getMenuItem().getCategory(),
                item.getMenuItem().getType(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());
            
        dto.setItems(itemDTOs);
        return dto;
    }
    
    
    public OrderResponseDTO cancelUserOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order không tồn tại"));
        
        // Kiểm tra quyền và trạng thái
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền huỷ order này");
        }
        
        if (!canCancelOrder(order.getStatus())) {
            throw new RuntimeException("Order không thể huỷ ở trạng thái: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        return convertToOrderResponseDTO(orderRepository.save(order));
    }

    // Helper method kiểm tra có thể huỷ không
    private boolean canCancelOrder(OrderStatus status) {
        return status != OrderStatus.COMPLETED && 
               status != OrderStatus.CANCELLED && 
               status != OrderStatus.SHIPPING;
    }

    // 2. Refactor code chung thành method riêng để tránh lặp lại
    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setStatus(order.getStatus().name());
        dto.setOrderDate(order.getOrderDate());
        dto.setCompletedDate(order.getCompletedDate());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        
        var itemDTOs = order.getOrderItems().stream()
            .map(item -> new OrderResponseDTO.OrderItemDTO(
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getMenuItem().getDescription(),
                item.getMenuItem().getImageUrl(),
                item.getMenuItem().getCategory(),
                item.getMenuItem().getType(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());
            
        dto.setItems(itemDTOs);
        return dto;
    }
    
    
}
