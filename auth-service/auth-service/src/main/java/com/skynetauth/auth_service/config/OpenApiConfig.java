package com.skynetauth.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class OpenApiConfig {

    /**
     * Create a configured OpenAPI instance for the application's REST API.
     *
     * @return an OpenAPI instance configured with a server at http://127.0.0.1:8080/auth-service, a security requirement referencing an HTTP Bearer (JWT) security scheme named "Bearer Authentication", and API metadata (title "SKYNET AUTH REST API", version "1.0").
     */
    @Bean
    public OpenAPI customOpenAPI() {
        
        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://127.0.0.1:8080/auth-service"); 
        gatewayServer.setDescription("API Gateway URL");

        return new OpenAPI()
            .servers(List.of(gatewayServer))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
            .info(new Info().title("SKYNET AUTH REST API").version("1.0"));
    }

    /**
     * Creates a SecurityScheme configured for HTTP Bearer JWT authentication.
     *
     * @return a SecurityScheme configured for HTTP Bearer authentication using JWT tokens
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
    }
}

