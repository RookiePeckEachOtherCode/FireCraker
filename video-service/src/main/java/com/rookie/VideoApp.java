package com.rookie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.rookie.mapper")
@EnableFeignClients
public class VideoApp {
    public static void main(String[] args) {
        SpringApplication.run(VideoApp.class, args);
    }
}
