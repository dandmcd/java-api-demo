package com.dantest.demo.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShowerQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShowerQueueApplication.class, args);
    }
}
