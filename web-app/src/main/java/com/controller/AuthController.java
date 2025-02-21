package com.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.dto.PatientDto;
import com.model.Credentials;
import com.model.User;
import com.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService; 


    @GetMapping("/login")
    public String showLoginPage( Model model) {
    	log.info("call login {}");
    	model.addAttribute("userCredential", new Credentials());
        return "/login"; 
    }


    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; 
    }


    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    @PostMapping(value = "/login")
    public Mono<String> postLogin(@ModelAttribute Credentials credentials,
    	HttpServletRequest request, HttpServletResponse response, Model model) {
    	
        return webClientBuilder.build().post()
                .uri("http://localhost:8080/login")
                .bodyValue(new Credentials(credentials.getUsername(), credentials.getPassword()))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jwt -> {
                    log.info("JWT reçu: {}", jwt);
                    response.addCookie(new Cookie("JWT", jwt)); 
                    response.addHeader("Authorization", "Bearer "+jwt);

                    return webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8080/patients")
                        .header("Authorization", "Bearer " + jwt)
                        .cookie("JWT", jwt)
                        .retrieve()
                        .bodyToMono(PatientDto[].class)
                        .map(Arrays::asList)
                        .doOnNext(patients -> patients.forEach(p -> log.info("Patient: {}", p)))
                        .map(patients -> {
                      	  	response.setHeader("Authorization", "Bearer " + jwt);
                            model.addAttribute("patients", patients);
                            return "index";
                        });
                })
                .onErrorResume(e -> {
                    log.error("Erreur lors du login ou de la récupération des patients", e);
                    model.addAttribute("error", "Échec de la connexion ou récupération des patients !");
                    model.addAttribute("userCredential", new Credentials());
                    return Mono.just("login");
                });
        	
    }


    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        boolean isRegistered = userService.register(user);

        if (isRegistered) {
            model.addAttribute("success", "User registered successfully");
            return "login"; 
        } else {
            model.addAttribute("error", "Registration failed");
            return "register"; 
        }
    }
}
