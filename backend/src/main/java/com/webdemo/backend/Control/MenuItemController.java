package com.webdemo.backend.Control;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.webdemo.backend.Servicee.MenuItemService;
import com.webdemo.backend.model.MenuItem;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;
	@Autowired
	private Cloudinary cloudinary;
	
	@GetMapping
	public List<MenuItem> getAllMenuItems() {
		return menuItemService.getAllMenuItems();
	}
	
//	   @GetMapping("/total")
//	    public long getTotalMenuItems() {
//	        return menuItemService.getMenuItemCount();
//	    }
//	   
	
	 @GetMapping("/drink")
		public List<MenuItem> getDrinks() {
			return menuItemService.getDrinks();
		}

	 @GetMapping("/food")
	 public ResponseEntity<List<MenuItem>> getFoods() {
	     return ResponseEntity.ok(menuItemService.getFoods());
	 }
	 
	 @GetMapping("/type/{type}")
	 public ResponseEntity<List<MenuItem>> getByTye(@PathVariable("type") String type) {
	     return ResponseEntity.ok(menuItemService.getByType(type));
	 }

	 
	 
	 	
	 @PostMapping("/upload")
	    public ResponseEntity<?> uploadMenuItem(
	            @RequestParam("file") MultipartFile file,
	            @RequestParam("name") String name,
	            @RequestParam("description") String description,
	            @RequestParam("price") double price,
	            @RequestParam("category") String category,
	            @RequestParam("type") String type

	    ) {
	        try {
	            // Upload ảnh lên Cloudinary
	            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
	            String imageUrl = (String) uploadResult.get("secure_url");

	            // Tạo đối tượng MenuItem và lưu
	            MenuItem item = new MenuItem();
	            item.setName(name);
	            item.setDescription(description);
	            item.setPrice(price);
	            item.setCategory(category);
	            item.setImageUrl(imageUrl);
	            item.setType(type);

	            MenuItem saved = menuItemService.createMenuItem(item);
	            return ResponseEntity.ok(saved);
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi upload ảnh: " + e.getMessage());
	        }
	    }





	

	@GetMapping("/{id}")
	public MenuItem getMenuItemById(@PathVariable("id") Long id) {
		return menuItemService.getMenuItemById(id);
	}

//	@PostMapping
//	public MenuItem createMenuItem(@RequestBody MenuItem menuItem) {
//		return menuItemService.createMenuItem(menuItem);
//	}

//	@PutMapping("/{id}")
//	public MenuItem updateMenuItem(@PathVariable("id") Long id, @RequestBody MenuItem updatedItem) {
//		return menuItemService.updateMenuItem(id, updatedItem);
//	}
//	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateMenuItem(
	    @PathVariable Long id,
	    @RequestParam(value = "file", required = false) MultipartFile file,
	    @RequestParam("name") String name,
	    @RequestParam("description") String description,
	    @RequestParam("price") double price,
	    @RequestParam("category") String category,
	    @RequestParam("type") String type
	) {
	    try {
	        // Tìm menu item theo ID
	        MenuItem existingItem = menuItemService.getMenuItemById(id);
	        if (existingItem == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Không tìm thấy menu item với ID: " + id);
	        }

	        // Cập nhật thông tin cơ bản
	        existingItem.setName(name);
	        existingItem.setDescription(description);
	        existingItem.setPrice(price);
	        existingItem.setCategory(category);
	        existingItem.setType(type);

	        // Nếu có file ảnh mới thì upload và cập nhật URL
	        if (file != null && !file.isEmpty()) {
	            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
	            String newImageUrl = (String) uploadResult.get("secure_url");
	            existingItem.setImageUrl(newImageUrl);
	        }
	        // Nếu không có file mới, giữ nguyên URL ảnh cũ

	        // Lưu menu item đã cập nhật
	        MenuItem updatedItem = menuItemService.updateMenuItem(existingItem);
	        return ResponseEntity.ok(updatedItem);
	        
	    } catch (IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Lỗi upload ảnh: " + e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("Lỗi cập nhật menu item: " + e.getMessage());
	    }
	}
	
	

	

	@DeleteMapping("/{id}")
	public void deleteMenuItem(@PathVariable("id") Long id) {
		menuItemService.deleteMenuItem(id);
	}
	
	
	
	

}
