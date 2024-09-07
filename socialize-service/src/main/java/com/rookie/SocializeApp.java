package com.rookie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rookie.mapper")
public class SocializeApp {
    public static void main(String[] args) {
        SpringApplication.run(SocializeApp.class, args);
    }
}
