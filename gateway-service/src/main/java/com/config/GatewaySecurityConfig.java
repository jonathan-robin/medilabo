//
//package com.config;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import io.netty.handler.codec.http2.Http2SecurityUtil;
//
//@Configuration
//
//public class GatewaySecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(Http2SecurityUtil http) throws Exception {
//        http
//            .csrf().disable()  // Désactive CSRF si non nécessaire
//            .authorizeRequests()
//            .requestMatchers("/login", "/public/**")  // Permet l'accès sans authentification pour ces chemins
//            .permitAll()
//            .anyRequest().authenticated();  // Authentification requise pour toutes les autres requêtes
//        return http.build();
//    }
//}
