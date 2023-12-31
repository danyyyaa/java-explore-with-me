package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum"})
public class MainServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ru.practicum.main.MainServiceApplication.class, args);
    }
}