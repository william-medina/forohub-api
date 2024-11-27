package com.williammedina.forohub.infrastructure.security;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String FRONTEND_URL = dotenv.get("FRONTEND_URL");

    public static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint("/api/auth/login", HttpMethod.POST),
            new PublicEndpoint("/api/auth/create-account", HttpMethod.POST),
            new PublicEndpoint("/api/auth/request-code", HttpMethod.POST),
            new PublicEndpoint("/api/auth/forgot-password", HttpMethod.POST),
            new PublicEndpoint("/api/auth/update-password/{token}", HttpMethod.POST),
            new PublicEndpoint("/api/auth/confirm-account/{token}", HttpMethod.GET),
            new PublicEndpoint("/api/topic", HttpMethod.GET),
            new PublicEndpoint("/api/topic/{topicId}", HttpMethod.GET),
            new PublicEndpoint("/api/response/{responseId}", HttpMethod.GET),
            new PublicEndpoint("/api/course", HttpMethod.GET),
            new PublicEndpoint("/api/docs", HttpMethod.GET),
            new PublicEndpoint("/api/docs/swagger-config", HttpMethod.GET),
            new PublicEndpoint("/api/docs/swagger-ui/**", HttpMethod.GET),
            new PublicEndpoint("/api/v3/api-docs/**", HttpMethod.GET)
    );

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    PUBLIC_ENDPOINTS.forEach(endpoint ->
                            auth.requestMatchers(endpoint.method(), endpoint.url()).permitAll()
                    );
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


    // Configuración de CORS dentro de la seguridad
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(FRONTEND_URL));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
