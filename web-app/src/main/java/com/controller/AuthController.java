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
import com.service.AuthService;
import com.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class AuthController {


    private final AuthService authService;
    @Autowired
    private UserService userService; // Service pour vérifier les credentials

    // Route pour l'affichage du formulaire de login
    @GetMapping("/login")
    public String showLoginPage( Model model) {
    	log.info("call login {}");
    	model.addAttribute("userCredential", new Credentials());
        return "/login"; // Retourner la vue login.html
    }

    // Route pour l'affichage du formulaire d'inscription
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Retourner la vue register.html
    }


    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthController(WebClient.Builder webClientBuilder, AuthService authService) {
        this.webClientBuilder = webClientBuilder;
        this.authService = authService;
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
//                    session.setAttribute("JWT", jwt);
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
                      	  	
                            // Création d'un cookie contenant le JWT
//                            Cookie cookie = new Cookie("JWT", jwt);
//                            cookie.setHttpOnly(true); // Assure que le cookie ne peut pas être accédé via JavaScript (sécurisé)
//                            cookie.setSecure(true);   // Assure que le cookie est envoyé uniquement sur des connexions HTTPS (à activer en production)
//                            cookie.setPath("/");      // Le cookie sera valable pour toute l'application
//                            cookie.setMaxAge(60 * 60); // Durée de vie du cookie en secondes (ici 1 heure)
//                            response.addCookie(cookie); // Aj
//                        	session.setAttribute("JWT", jwt);
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

 

    
    // Endpoint pour traiter la connexion de l'utilisateur
//    @PostMapping("/login")
//    public Mono<Object> login(@RequestParam String username, WebSession session, @RequestParam String password, Model model) {
//    	
//    	// call auth-service to get jwtoken to identify user
//        return webclient.post().uri("/login")
//                        .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(new Credentials(username, password)))
//                        .exchangeToMono(res -> {
//                                if (res.statusCode().equals(HttpStatus.OK)) {
//
//                                        session.getAttributes().put("Authorization", res.headers()
//                                                        .header("Authorization").get(0)
//                                                        .replace("Bearer ", ""));
//
//                                        return Mono.just(Rendering.redirectTo("index").build());
//                                }
//
//                                return Mono.just(Rendering.redirectTo("login").build());
//
//                        });
    	
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String token = jwtUtil.generateToken(authentication.getName());
//        model.addAttribute("token", token);
//        return "redirect:/";  // Redirige vers la page d'accueil après login
        
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        
//        // Générer le JWT
//        String token = jwtUtil.generateToken(user.getUsername());
//        log.info("jwtToken {}", token);
//
//        // Stocker le JWT dans un cookie HTTP-only (empêche l’accès JS pour plus de sécurité)
//        Cookie cookie = new Cookie("jwtToken", token);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        response.addCookie(cookie);


//          return "index"; // Rediriger vers le dashboard une fois authentifié
            // Ajouter le token à la vue

//    }

    // Endpoint pour gérer l'inscription de l'utilisateur
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        boolean isRegistered = userService.register(user); // Inscrire l'utilisateur

        if (isRegistered) {
            model.addAttribute("success", "User registered successfully");
            return "login"; // Rediriger vers la page de login après l'inscription réussie
        } else {
            model.addAttribute("error", "Registration failed");
            return "register"; // Renvoyer vers la page d'inscription en cas d'échec
        }
    }
}
