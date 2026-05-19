package com.bookkeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookkeepingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookkeepingApplication.class, args);
    }
}