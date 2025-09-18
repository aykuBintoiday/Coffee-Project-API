package com.coffee.coffee_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CoffeeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeApiApplication.class, args);
    }

    // 👉 thêm CommandLineRunner để in ra hash của mật khẩu Admin@123
    @Bean
    CommandLineRunner generatePassword() {
        return args -> {
            var encoder = new BCryptPasswordEncoder();
            String raw = "Admin@123";   // mật khẩu bạn muốn đặt
            String hash = encoder.encode(raw);
            System.out.println("Hash cho '" + raw + "': " + hash);
        };
    }
}
