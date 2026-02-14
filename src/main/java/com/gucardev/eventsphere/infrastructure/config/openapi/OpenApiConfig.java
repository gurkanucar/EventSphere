package com.gucardev.eventsphere.infrastructure.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(description = "OpenApi documentation", title = "Spring Boot Project", version = "1.0"),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        },
        servers = {
                @Server(url = "http://localhost:8080", description = "localhost"),
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerAuth",
                description = "JWT auth description",
                scheme = "bearer",
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                in = SecuritySchemeIn.HEADER
        )
})
@Configuration
public class OpenApiConfig {

}
