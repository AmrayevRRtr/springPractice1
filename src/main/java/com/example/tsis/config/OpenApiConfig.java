package com.example.tsis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tsisOpenApi(AppProperties props) {
        return new OpenAPI().info(new Info()
                .title("TSIS Booking API")
                .version("v1")
                .description("REST API for room bookings. Errors are returned as RFC 7807 problem details.")
                .contact(new Contact().email(props.supportEmail())));
    }
}
