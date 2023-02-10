package com.example.springredisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableCaching
public class SpringRedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRedisDemoApplication.class, args);
    }

}
