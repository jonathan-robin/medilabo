package com.filter;

import java.util.Base64;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
//import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HeaderFilter extends AbstractGatewayFilterFactory<HeaderFilter.Config> {

    private final Environment environment;

    @Autowired
    public HeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
    }

    public static class Config {
        // Configurations can be added here if necessary
    }




//        private String extractJwtFromCookie(ServerHttpRequest request) {
//            return request.getCookies().getFirst("JWT") != null ?
//                    request.getCookies().getFirst("JWT").getValue() : null;
//        }
//    }

//    
//    @Override
//    public GatewayFilter apply(Config config) {
 
        @Override
        public GatewayFilter apply(Config config) {
//            return (exchange, chain) -> {
//            	
//            	String path = exchange.getRequest().getURI().getPath();
//
//    	        if (path.equals("/login")) 
//    	            return chain.filter(exchange);
//    	        
//                ServerHttpRequest request = exchange.getRequest();
//                String jwt = extractJwtFromCookie(request);
//
//                if (!isJwtValid(jwt)) 
//                    return onError(exchange.getResponse(), "Invalid JWT token", HttpStatus.UNAUTHORIZED);
//                
//                if (jwt != null) {
//                    ServerHttpRequest modifiedRequest = request.mutate()
//                            .header("Authorization", "Bearer " + jwt)
//                            .build();
//
//                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
//                }
//                return chain.filter(exchange);
//            };
//        }
//    	
//    	
//    	log.info("apply: {}", config);
        return (exchange, chain) -> {
        	
        	
//        	if (exchange.getRequest().getURI().getPath().equals(config))
//        	
        	log.info("request path: {}", exchange.getRequest().getURI().getPath());


        	 
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Request Headers: {}", request.getHeaders());
            String jwt = null;
            
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
            for (String key : cookies.keySet()) {
                for (HttpCookie cookie : cookies.get(key)) {
                    if ("JWT".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                    }
                }
            }
//
//            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                return onError(response, "No Authorization header", HttpStatus.UNAUTHORIZED);
//            }

//            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            log.info("authorizationHeader= {}", authorizationHeader);
//
//            if (!authorizationHeader.startsWith("Bearer ")) {
//                return onError(response, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
//            }
//
//            String jwt = authorizationHeader.replace("Bearer ", "");
            if (!isJwtValid(jwt)) {
            	log.info("wrong JWT token");
            	return onError(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
            
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        String tokenSecret = environment.getProperty("token.secret.key");
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        var secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        
        log.info("tokenSecret={}",tokenSecret);

        try {
         
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)  
                    .build() 
                    .parseClaimsJws(jwt)  
                    .getBody(); 

            
            log.info("claims: {}",claims );
            log.info("claims != null: {}",claims != null );
            log.info("laims.get(\"jti\") != null: {}",claims.get("jti") != null );
            
            return claims != null;
        } catch (Exception e) {
        	log.info("jwt={}, exception={}",jwt, e);
            return false;
        }
    }

}



