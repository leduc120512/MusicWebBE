// src/main/java/com/musicapi/config/StaticResourceConfig.java
package com.musicapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

// StaticResourceConfig.java
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    /** Filesystem root that /upload/** is served from; see app.upload.dir. */
    @Value("${app.upload.dir}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(Paths.get(uploadRoot).toUri().toString());
    }
}
