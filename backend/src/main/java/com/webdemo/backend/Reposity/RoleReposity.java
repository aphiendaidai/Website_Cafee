package com.webdemo.backend.Reposity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdemo.backend.model.Role;


@Repository
public interface RoleReposity extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
