package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${upload.path:C:/Users/Praveen/Downloads}")
    private String uploadBasePath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose uploads directory for static file access
        String uploadPath = uploadBasePath + "/uploads/books";
        registry.addResourceHandler("/uploads/books/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
