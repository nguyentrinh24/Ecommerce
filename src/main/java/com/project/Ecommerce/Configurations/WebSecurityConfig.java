package com.project.Ecommerce.Configurations;

import com.project.Ecommerce.Fillter.JwtFilter;
import com.project.Ecommerce.Model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (permitAll)
                        .requestMatchers(HttpMethod.GET, "/api/v1/role").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/order/**").permitAll()
                        .requestMatchers("/api/v1/products/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/v1/products/generateProductFake").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/order_details/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/order_details/order/**").permitAll()
                        // order-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/order/**").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/order/**").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/order/**").hasAnyRole(Role.ADMIN,Role.USER)
                        // products-specific endpoints
                                .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole(Role.ADMIN)
                        // categories-specific endpoints
                          .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole(Role.ADMIN)
                        // order_detail-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/order_details/**").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/order_details/**").hasAnyRole(Role.ADMIN,Role.USER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/order_details/**").hasAnyRole(Role.ADMIN,Role.USER)
                        // login
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/detail").hasAnyRole(Role.ADMIN,Role.USER)
                        .anyRequest().authenticated()
                );


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:60692"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
