package com.webdemo.backend.Reposity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdemo.backend.model.MenuItem;

@Repository
public interface MenuItemReposity extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByCategory(String category);

    List<MenuItem> findByType(String type);
}
