package com.williammedina.forohub.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("cookieAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("accessToken")
                                        .description("🚀 Authentication is handled automatically using cookies. You do not need to enter anything manually.")
                        ))
                .info(new Info()
                        .title("ForoHub API")
                        .version("1.0")
                        .description("⚠️ This API uses cookie-based authentication. Once you log in, protected endpoints will automatically become accessible.")
                );
    }
}
