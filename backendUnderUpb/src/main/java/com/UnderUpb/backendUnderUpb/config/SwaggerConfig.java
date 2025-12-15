package com.UnderUpb.backendUnderUpb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UnderUpb Game Backend API")
                        .version("1.0")
                        .description("Complete API documentation for UnderUpb game backend - Users, Questions, Answers, Purchases, Characters, Levels, Decisions, Leaderboard, Matches, and Save Games")
                        .contact(new Contact()
                                .name("UnderUpb Team")
                                .url("http://localhost:8081"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token for API authentication")))
                .tags(java.util.Arrays.asList(
                        new io.swagger.v3.oas.models.tags.Tag().name("Upbolis - Webhooks").description("Endpoints to receive and test webhooks from Upbolis"),
                        new io.swagger.v3.oas.models.tags.Tag().name("Upbolis - Users").description("Endpoints for user authentication and status with Upbolis"),
                        new io.swagger.v3.oas.models.tags.Tag().name("Upbolis - Products").description("Endpoints to sync products between local system and Upbolis"),
                        new io.swagger.v3.oas.models.tags.Tag().name("Upbolis - Purchases").description("Endpoints for purchases and purchase verification using Upbolis")
                ));
    }
}