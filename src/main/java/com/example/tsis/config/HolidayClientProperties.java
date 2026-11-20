package com.example.tsis.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.holiday-client")
public record HolidayClientProperties(

        @NotBlank String baseUrl,
        @NotBlank String countryCode,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}
