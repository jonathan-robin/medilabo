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
    	
    	if (exchange.getRequest().getURI().getPath().equals("/login"))
    		return chain.filter(exchange);
    	
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);  

            exchange = exchange.mutate()
                .request(r -> r.headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)))
                .build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;  
    }
}
