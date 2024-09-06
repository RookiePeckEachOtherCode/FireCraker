package com.rookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FileApp {
    public static void main(String[] args) {
        SpringApplication.run(FileApp.class, args);
    }
}
