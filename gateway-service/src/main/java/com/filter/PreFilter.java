package com.filter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PreFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Récupérer l'en-tête Authorization qui contient le JWT
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extraire le JWT du header Authorization
            String jwtToken = authorizationHeader.substring(7);  // Retirer "Bearer " du début du token

            log.info("JWT Token found: {}", jwtToken);

            // Ajouter le JWT dans les en-têtes de la requête sortante vers les autres microservices
            exchange = exchange.mutate()
                .request(r -> r.headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)))
//                .response(r -> r.header(headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)))
                .build();
        } else {
            log.warn("No JWT token found in the request");
        }

        // Passer la requête au prochain filtre dans la chaîne
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;  // Priorité du filtre, 0 étant le premier
    }
}
