package com.webdemo.backend.model;

public class OrderItemRequestDTO {
    private Long menuItemId;
    private int quantity;
    private String size;  // ← Thêm field này
    
    // Constructors
    public OrderItemRequestDTO() {}
    
    public OrderItemRequestDTO(Long menuItemId, int quantity, String size) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.size = size;
    }
    
    // Getters and Setters
    public Long getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getSize() {  // ← Thêm getter
        return size;
    }
    
    public void setSize(String size) {  // ← Thêm setter
        this.size = size;
    }
    
    @Override
    public String toString() {
        return String.format("OrderItemRequestDTO{menuItemId=%d, quantity=%d, size='%s'}", 
                           menuItemId, quantity, size);
    }
}