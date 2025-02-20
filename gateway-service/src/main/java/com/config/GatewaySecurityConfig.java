//
//package com.config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class GatewaySecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()  // Désactive CSRF si non nécessaire
//            .authorizeRequests()
//            .requestMatchers("/login", "/public/**")  // Permet l'accès sans authentification pour ces chemins
//            .permitAll()
//            .anyRequest().authenticated();  // Authentification requise pour toutes les autres requêtes
//        return http.build();
//    }
//}
