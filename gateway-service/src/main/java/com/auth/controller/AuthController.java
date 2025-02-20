//package com.auth.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import com.auth.service.AuthService;
//import com.model.Credentials;
//
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Mono;
//
//@Controller
//@Slf4j
//public class AuthController {
//	
//	
//	private final AuthService auth; 
//	
//	public AuthController(AuthService auth) {
//		this.auth = auth;
//	}
//
//    @PostMapping("/login")
//    public ResponseEntity<Mono<String>> login(@RequestBody Credentials credentials) {
//    	
//        log.info("Intercept request POST /login : username={}, password={}", credentials.getUsername(), credentials.getPassword());
//        if ("user".equals(credentials.getUsername()) && "pass".equals(credentials.getPassword())) {
//        	Mono<String> jwt = auth.login(credentials.getUsername(), credentials.getPassword());
//        	return ResponseEntity.ok(jwt);
//        }	
//        else
//        	throw new RuntimeException("Authentication failed");
//        
//        
//    }
//
//	
//}
