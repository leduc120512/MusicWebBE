package com.musicapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MusicApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicApiApplication.class, args);
    }
}
