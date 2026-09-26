package com.example.tsis.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(

        @NotBlank(message = "app.name must not be blank")
        String name,

        @NotBlank(message = "app.environment must not be blank")
        String environment,

        @Positive(message = "app.max-items must be a positive number")
        int maxItems,

        @Email(message = "app.support-email must be a valid email address")
        String supportEmail
) {
}
