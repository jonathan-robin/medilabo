package com.filter;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HeaderFilter extends AbstractGatewayFilterFactory<HeaderFilter.Config> {

    @Value("${jwt.secret}")
    private String tokenSecret; 

    public HeaderFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
             
            ServerHttpResponse response = exchange.getResponse();

            String jwt = null;
            
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
            for (String key : cookies.keySet()) {
                for (HttpCookie cookie : cookies.get(key)) {
                    if ("JWT".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                    }
                }
            }
            
            if (!isJwtValid(jwt))
            	return onError(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        var secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)  
                    .build() 
                    .parseClaimsJws(jwt)  
                    .getBody(); 
            
            return claims != null;
        } catch (Exception e) {
            log.error("Invalid JWT Token: ", e); 
            return false;
        }
    }
}
