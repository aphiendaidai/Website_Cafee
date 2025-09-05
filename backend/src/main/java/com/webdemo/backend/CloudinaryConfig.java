package com.webdemo.backend;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
	@Bean
	  public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dqqbvzmrr");
        config.put("api_key", "789644833813378");
        config.put("api_secret", "15CW3h71RPsMQWIJ60tIVkRhbO0");
        return new Cloudinary(config);
    }	
	
}
