package com.rido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@OpenAPIDefinition(info = @Info(title = "Rido APIS", version = "1.0", description = "This API provides endpoints for managing rido resources"))
public class RidoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RidoAppApplication.class, args);
    }
}


