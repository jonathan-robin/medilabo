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
public class PostFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    	
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String jwt = headers.getFirst(HttpHeaders.AUTHORIZATION);
            
            log.info("jwt in gateway svc: {}", jwt);

            if (jwt != null && jwt.startsWith("Bearer ")) {
                String token = jwt.substring(7); // Retire "Bearer "

            exchange.getResponse().getHeaders().add("X-JWT-TOKEN", token);
            exchange.getResponse().getHeaders().add("Authorization", "Bearer "+token);
            
            }

        }));
    }


    @Override
    public int getOrder() {
        return 12;
    }

}