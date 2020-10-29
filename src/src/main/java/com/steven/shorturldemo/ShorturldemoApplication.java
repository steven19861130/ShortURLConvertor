package com.steven.shorturldemo;

import com.steven.shorturldemo.service.ShortURLConvertorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class ShorturldemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShorturldemoApplication.class, args);
    }

}
