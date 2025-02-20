//package com.auth.service;
//
//import java.util.Base64;
//import java.util.Date;
//import javax.crypto.SecretKey;
//
//import org.springframework.stereotype.Service;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Service
//@Slf4j
//public class AuthService {
//
//    private static final String SECRET_KEY = "7DA2220C0016000C0047BB08F1F84BCD208F369A45AE16D5CC27E464FCE388A0";
//    private static final long EXPIRATION_MS = 86400000; 
//
//    public Mono<String> login(String username, String password) {
//        log.info("username={}", username);
//        log.info("password={}", password);
//
//        if (isValidUser(username, password)) {
//            return Mono.just(generateJwt(username)); 
//        } else {
//            return Mono.error(new RuntimeException("Invalid credentials"));
//        }
//    }
//
//    private boolean isValidUser(String username, String password) {
//        return "user".equals(username) && "pass".equals(password);  
//    }
//
//    public static String generateJwt(String username) {
//        byte[] secretKeyBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
//        SecretKey key = Keys.hmacShaKeyFor(secretKeyBytes);
//        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_MS);
//
//        log.info("Generated JWT key: {}", key);  
//        log.info("JWT expiration: {}", expirationDate);
//        
//        return Jwts.builder()
//                .setSubject(username)  
//                .setIssuedAt(new Date())  
//                .setExpiration(expirationDate)
//                .claim("role", "USER")  
//                .signWith(key, SignatureAlgorithm.HS256) 
//                .compact();
//    }
//}
