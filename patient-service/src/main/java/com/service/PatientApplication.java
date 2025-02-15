package com.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class PatientApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientApplication.class, args);
    }
}