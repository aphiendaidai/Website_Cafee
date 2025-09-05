package com.webdemo.backend.model;

import java.util.List;

public class OrderRequestDTO {
    private Long userId;
    private String shippingAddress;
    private String phoneNumber;
    private List<OrderItemRequestDTO> items;
    
    
	public OrderRequestDTO() {
		super();
	}


	public OrderRequestDTO(Long userId, String shippingAddress, String phoneNumber, List<OrderItemRequestDTO> items) {
		super();
		this.userId = userId;
		this.shippingAddress = shippingAddress;
		this.phoneNumber = phoneNumber;
		this.items = items;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getShippingAddress() {
		return shippingAddress;
	}


	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public List<OrderItemRequestDTO> getItems() {
		return items;
	}


	public void setItems(List<OrderItemRequestDTO> items) {
		this.items = items;
	}
    
   
    


}
