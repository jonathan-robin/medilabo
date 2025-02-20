package com.filter;

import java.util.Set;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
//import lombok.extern.slf4j.Slf4j;
//import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


import java.util.Set;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PostFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String jwt = headers.getFirst(HttpHeaders.AUTHORIZATION);
            
            log.info("jwt in gateway svc: {}", jwt);

            if (jwt != null && jwt.startsWith("Bearer ")) {
                String token = jwt.substring(7); // Retire "Bearer "

                // 🔹 Ajout du JWT en tant que Cookie
//                exchange.getResponse().addCookie(ResponseCookie.from("JWT", token)
//                        .httpOnly(true)  // Sécurité : empêche l'accès via JS
//                        .secure(true)    // Seulement en HTTPS
//                        .path("/")       // Disponible sur tout le site
//                        .sameSite("Strict")
//                        .build()
//                );

                // 🔹 Ajout du JWT dans les headers de réponse (si besoin)
                exchange.getResponse().getHeaders().add("X-JWT-TOKEN", token);
                exchange.getResponse().getHeaders().add("Authorization", "Bearer "+token);
            }

            log.info("Post Filter - JWT ajouté dans la réponse.");

        }));
    }


    @Override
    public int getOrder() {
        return 12;
    }

}