package com.webdemo.backend.model;

public enum OrderStatus {
    PENDING("Đang chờ xử lý"),
    SHIPPING("Đang giao"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");
    
    private final String description;	
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name() + " (" + description + ")";
    }
}
