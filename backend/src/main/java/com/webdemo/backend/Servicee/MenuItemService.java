package com.webdemo.backend.Servicee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webdemo.backend.Reposity.MenuItemReposity;
import com.webdemo.backend.model.MenuItem;

import jakarta.transaction.Transactional;

@Service
@Transactional 
public class MenuItemService {
    @Autowired
    private MenuItemReposity menuItemReposity;
    
    

    public MenuItemService(MenuItemReposity menuItemReposity) {
		super();
		this.menuItemReposity = menuItemReposity;
	}

	public List<MenuItem> getAllMenuItems() {
        return menuItemReposity.findAll();
    }
	
	
	  public long getMenuItemCount() {
	        return menuItemReposity.count();
	    }
	
	public List<MenuItem> getDrinks() {
	    return menuItemReposity.findByCategory("drink");
	}

	public List<MenuItem> getFoods() {
	    return menuItemReposity.findByCategory("food");
	}
	
	public List<MenuItem> getCaphe() {
	    return menuItemReposity.findByType("ca-phe");
	}

	public List<MenuItem> getTraTraiCay() {
	    return menuItemReposity.findByType("tra-trai-cay");
	}

	public List<MenuItem> getTraChanh() {
	    return menuItemReposity.findByType("tra-chanh");
	}

	public List<MenuItem> getBanhNgot() {
	    return menuItemReposity.findByType("banh-ngot");
	}

	public List<MenuItem> getBanhMi() {
	    return menuItemReposity.findByCategory("banh-mi");
	}

	public List<MenuItem> getSinhTo() {
	    return menuItemReposity.findByType("sinh-to");
	}

	public List<MenuItem> getNuocEp() {
	    return menuItemReposity.findByCategory("nuoc-ep");
	}

	public List<MenuItem> getTraSua() {
	    return menuItemReposity.findByType("tra-sua");
	}

	public List<MenuItem> getNuocNgot() {
	    return menuItemReposity.findByType("nuoc-ngot");
	}

	public List<MenuItem> getDoAnVat() {
	    return menuItemReposity.findByType("do-an-vat");
	}
	

	public List<MenuItem> getByType(String type) {
	    return menuItemReposity.findByType(type);
	}

	
	


	
	
    @Transactional
	 public MenuItem getMenuItemById(Long id) {
	        return menuItemReposity.findById(id)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy món"));
	    }


    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemReposity.save(menuItem);
    }
    
    
//    @Transactional // Thêm dòng này
//    public MenuItem updateMenuItem(Long id, MenuItem upMenuItem) {
//    	MenuItem item = getMenuItemById(id);
//    	item.setName(upMenuItem.getName());
//    	item.setDescription(upMenuItem.getDescription());
//    	item.setPrice(upMenuItem.getPrice());
//    	item.setImageUrl(upMenuItem.getImageUrl());
//    	item.setCategory(upMenuItem.getCategory());
//    	item.setType(upMenuItem.getType());
//    	return menuItemReposity.save(item);
//    	
//    }
    
    @Transactional
    public MenuItem updateMenuItem(MenuItem menuItem) {
        return menuItemReposity.save(menuItem);
    }
    
    public void deleteMenuItem(Long id) {
    	menuItemReposity.deleteById(id);
    }
        
}
