package com.seateam.secondhand_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.seateam.secondhand_system.mapper")
@SpringBootApplication
public class SecondhandSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondhandSystemApplication.class, args);
    }

}
