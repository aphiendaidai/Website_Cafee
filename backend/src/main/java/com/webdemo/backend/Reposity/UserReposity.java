package com.webdemo.backend.Reposity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdemo.backend.model.AuthProvider;
import com.webdemo.backend.model.User;


@Repository
public interface UserReposity extends JpaRepository<User, Long>{
	
	  Optional<User> findByUsername(String username);
	    Optional<User> findByEmail(String email);
	    boolean existsByUsername(String username);
	    boolean existsByEmail(String email);
	    Optional<User> findByGoogleId(String googleId);
	     Optional<User> findByEmailAndProvider(String email, AuthProvider provider );

}
