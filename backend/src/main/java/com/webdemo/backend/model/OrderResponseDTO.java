package com.webdemo.backend.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
	private Long orderId;
	private String shippingAddress;
	private String phoneNumber;
	private String status;
	private LocalDateTime orderDate;
	private LocalDateTime completedDate;
	private List<OrderItemDTO> items;
	private Long userId;
	private String username;
	
	
	public static class OrderItemDTO {
		private Long menuItemId;
		private String menuItemName;
		private String menuItemDescription;
		private String imageUrl;
		private String category;
		private String type;
		private int quantity;
		private double price;

		public OrderItemDTO(Long menuItemId, String menuItemName, String menuItemDescription, String imageUrl,
				String category, String type, int quantity, double price) {
			this.menuItemId = menuItemId;
			this.menuItemName = menuItemName;
			this.menuItemDescription = menuItemDescription;
			this.imageUrl = imageUrl;
			this.category = category;
			this.type = type;
			this.quantity = quantity;
			this.price = price;
		}

		// getters & setters
		public Long getMenuItemId() {
			return menuItemId;
		}

		public void setMenuItemId(Long menuItemId) {
			this.menuItemId = menuItemId;
		}

		public String getMenuItemName() {
			return menuItemName;
		}

		public void setMenuItemName(String menuItemName) {
			this.menuItemName = menuItemName;
		}

		public String getMenuItemDescription() {
			return menuItemDescription;
		}

		public void setMenuItemDescription(String menuItemDescription) {
			this.menuItemDescription = menuItemDescription;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}
	}

	// getters & setters
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public LocalDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDateTime completedDate) {
		this.completedDate = completedDate;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
