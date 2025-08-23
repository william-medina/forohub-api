package com.williammedina.forohub.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("🔑 Use a valid JWT access token in the Authorization header: `Bearer <token>`")
                        ))
                .info(new Info()
                        .title("ForoHub API")
                        .version("1.0")
                        .description("""
                            API para foros de discusión basada en cursos.
                            Permite gestionar tópicos, respuestas, perfiles de usuario y notificaciones. 
                            La autenticación utiliza Access Token y Refresh Token:
                            - El Access Token se devuelve en el login y debe enviarse en el header `Authorization: Bearer {token}`.
                            - El Refresh Token se almacena en una cookie HttpOnly y permite obtener un nuevo Access Token en el endpoint de refresh-token.
                            """)
                );
    }
}
