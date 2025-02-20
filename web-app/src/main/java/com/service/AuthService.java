package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.model.Credentials;

import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> login(String username, String password) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/login")  // Route dans gateway-service
                .bodyValue(new Credentials(username, password))  // Ton objet avec username et password
                .retrieve()
                .bodyToMono(String.class)  // Réponse du JWT sous forme de String
                .onErrorResume(e -> Mono.error(new Exception("Login failed", e)));
    }
}
