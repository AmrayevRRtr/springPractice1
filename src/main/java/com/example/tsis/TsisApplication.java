package com.example.tsis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TsisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TsisApplication.class, args);
    }
}
