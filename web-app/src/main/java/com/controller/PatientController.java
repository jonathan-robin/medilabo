package com.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.dto.PatientDto;
import com.model.Credentials;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/patients")
@Slf4j
public class PatientController {

    private final WebClient webClient;

    @Autowired
    public PatientController(WebClient webClient) {
        this.webClient = webClient;
    }
    
    @GetMapping("")
    public Mono<String> getAllPatients(Model model, HttpServletRequest request, HttpServletResponse response) {
    
    	String jwtFromHeader = null;
    	String jwtFromCookie = null;
    	String jwt = null;
    	
    	if (request.getHeader("Authorization") != null)
    		jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", ""); 
    	
    	Cookie[] cookies = request.getCookies();
    	for (Cookie cookie: cookies) { 
    		if (cookie.getName().equals("JWT")) {
    			jwtFromCookie = cookie.getValue();			
    		}
    	}
    	log.info("JWT from Header: {}", jwtFromHeader);
    	log.info("JWT from Cookie: {}", jwtFromCookie);
    	if (jwtFromCookie != null)
    		jwt = jwtFromCookie; 
    	else if (jwtFromHeader != null)
    		jwt = jwtFromHeader;
//    	jwt = jwtFromCookie != null ? jwtFromCookie : jwtFromHeader;
    	log.info("jwt computed: {}", jwt);
//    	log.info("JWT from Cookie: {}", jwtFromCookie);
    	
    	
    	if (jwt == null && jwtFromCookie == null && jwt == null) {
    		log.info("No auth available");
    		return Mono.just("login");
    	}
    	
    	log.info("/patients jwt: {}", jwt);
    	log.info("/patients jwtCookie: {}", jwtFromCookie);
    	
    	response.addHeader("Authorization", "Bearer "+jwtFromCookie);
//    	response.addCookie(new Cookie("jwt", jwt));
    	
        return webClient.get()
                .uri("/patients")
                .header("Authorization", "Bearer " +  jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientDto[].class)
                .map(Arrays::asList)
                .doOnNext(patients -> log.info("Patients récupérés : {}", patients))
                .map(patients -> {
                	String _jwtFromCookie = null;
                	for (Cookie cookie: cookies) { 
                		if (cookie.getName().equals("JWT")) {
                			 _jwtFromCookie = cookie.getValue();			
                		}
                	}
//                	Cookie cookie = new Cookie("JWT", jwt);
//                	
//                	response.addCookie(cookie);
                	response.setHeader("Authorization", "Bearer " + _jwtFromCookie);
                    model.addAttribute("patients", patients);
                    return "index";
                })
                .onErrorResume(e -> {
                    log.error("Erreur lors de la récupération des patients", e);
                    model.addAttribute("userCredential", new Credentials());
                    return Mono.just("redirect:/login");
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getPatientDetails(@PathVariable Long id, Model model, HttpServletRequest request) {

    	String jwtFromHeader = null;
    	String jwtFromCookie = null;
    	String jwt = null;
    	
    	if (request.getHeader("Authorization") != null)
    		jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", ""); 
    	
    	Cookie[] cookies = request.getCookies();
    	for (Cookie cookie: cookies) { 
    		if (cookie.getName().equals("JWT")) {
    			jwtFromCookie = cookie.getValue();			
    		}
    	}
    	log.info("JWT from Header: {}", jwtFromHeader);
    	log.info("JWT from Cookie: {}", jwtFromCookie);
    	if (jwtFromCookie != null)
    		jwt = jwtFromCookie; 
    	else if (jwtFromHeader != null)
    		jwt = jwtFromHeader;

    	if (jwt == null && jwtFromCookie == null && jwt == null) {
    		log.info("No auth available");
    		return Mono.just("login");
    	}

    	    log.info("JWT in patient controller /id : {}",jwt);
    	    return webClient.get()
    	            .uri("/patients/" + id)
    	            .header("Authorization", "Bearer " + jwt)
    	            .cookie("JWT", jwt)
    	            .retrieve()
    	            .bodyToMono(PatientDto.class)
    	            .doOnNext(patient -> log.info("Détails patient : {}", patient))
    	            .map(patient -> {
    	                model.addAttribute("patient", patient);
    	                return "patient-details";
    	            })
    	            .onErrorResume(e -> {
    	                log.error("Erreur lors de la récupération du patient", e);
    	                model.addAttribute("userCredential", new Credentials());
    	                return Mono.just("redirect:/login");
    	            });
    }

    @GetMapping("/new")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "patient-form";
    }
    
    
    @GetMapping("edit/{id}")
    public Mono<String> showUpdatetForm(@PathVariable Long id, Model model, HttpServletRequest request) {

    	    String jwtFromHeader = null;
    	    String jwtFromCookie = null;
    	    String jwt = null;

    	    if (request.getHeader("Authorization") != null)
    	        jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

    	    Cookie[] cookies = request.getCookies();
    	    for (Cookie cookie: cookies) {
    	        if (cookie.getName().equals("JWT")) {
    	            jwtFromCookie = cookie.getValue();
    	        }
    	    }
    	    log.info("JWT from Header: {}", jwtFromHeader);
    	    log.info("JWT from Cookie: {}", jwtFromCookie);
    	    if (jwtFromCookie != null)
    	        jwt = jwtFromCookie;
    	    else if (jwtFromHeader != null)
    	        jwt = jwtFromHeader;

    	    if (jwt == null && jwtFromCookie == null && jwt == null) {
    	        log.info("No auth available");
    	        return Mono.just("login");
    	    }

    	    log.info("JWT in patient controller /edit/{id} : {}", jwt);
    	    return webClient.get()
    	            .uri("/patients/" + id)
    	            .header("Authorization", "Bearer " + jwt)
    	            .cookie("JWT", jwt)
    	            .retrieve()
    	            .bodyToMono(PatientDto.class)
    	            .doOnNext(patient -> log.info("Détails patient : {}", patient))
    	            .map(patient -> {
    	                model.addAttribute("patient", patient);
    	                return "patient-edit-form";  // Nom du formulaire d'édition
    	            })
    	            .onErrorResume(e -> {
    	                log.error("Erreur lors de la récupération du patient", e);
    	                model.addAttribute("userCredential", new Credentials());
    	                return Mono.just("redirect:/login");
    	            });

    }
    

    @PostMapping("/edit/{id}")
    public Mono<String> editPatient(@ModelAttribute("patient") PatientDto patientDto, @PathVariable Long id, Model model,  HttpServletRequest request) {
    	
    	String jwtFromHeader = null;
 	    String jwtFromCookie = null;
 	    String jwt = null;

 	    if (request.getHeader("Authorization") != null)
 	        jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

 	    Cookie[] cookies = request.getCookies();
 	    for (Cookie cookie: cookies) {
 	        if (cookie.getName().equals("JWT")) {
 	            jwtFromCookie = cookie.getValue();
 	        }
 	    }
 	    log.info("JWT from Header: {}", jwtFromHeader);
 	    log.info("JWT from Cookie: {}", jwtFromCookie);
 	    if (jwtFromCookie != null)
 	        jwt = jwtFromCookie;
 	    else if (jwtFromHeader != null)
 	        jwt = jwtFromHeader;

 	    if (jwt == null && jwtFromCookie == null && jwt == null) {
 	        log.info("No auth available");
 	        return Mono.just("login");
 	    }

 	    log.info("JPOST WT in patient controller /edit/{id} : {}", jwt);
 	    return webClient.put()
 	            .uri("/patients/" + id)
                .bodyValue(patientDto)
 	            .header("Authorization", "Bearer " + jwt)
 	            .cookie("JWT", jwt)
 	            .retrieve()
 	            .bodyToMono(PatientDto.class)
 	            .doOnNext(patient -> log.info("Détails patient : {}", patient))
 	            .map(patient -> {
 	                model.addAttribute("patient", patient);
 	                return "patient-details";  // Nom du formulaire d'édition
 	            })
 	            .onErrorResume(e -> {
 	                log.error("Erreur lors de la récupération du patient", e);
 	                model.addAttribute("userCredential", new Credentials());
 	                return Mono.just("redirect:/login");
 	            });

    	
    	

    }
    
    // Sauvegarde ou mise à jour du patient
    @GetMapping("/delete/{id}")
    public Mono<String> deletePatient(@PathVariable Long id, Model model,  HttpServletRequest request) {
    	
    	String jwtFromHeader = null;
 	    String jwtFromCookie = null;
 	    String jwt = null;

 	    if (request.getHeader("Authorization") != null)
 	        jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

 	    Cookie[] cookies = request.getCookies();
 	    for (Cookie cookie: cookies) {
 	        if (cookie.getName().equals("JWT")) {
 	            jwtFromCookie = cookie.getValue();
 	        }
 	    }

 	    if (jwtFromCookie != null)
 	        jwt = jwtFromCookie;
 	    else if (jwtFromHeader != null)
 	        jwt = jwtFromHeader;

 	    if (jwt == null && jwtFromCookie == null && jwt == null) {
 	        log.info("No auth available");
 	        return Mono.just("login");
 	    }

 	    log.info("JPOST WT in patient controller /delete/{id} : {}", jwt);
 	    return webClient.delete()
 	            .uri("/patients/" + id)
 	            .header("Authorization", "Bearer " + jwt)
 	            .cookie("JWT", jwt)
 	            .retrieve()
 	            .bodyToMono(PatientDto[].class)
 	            .map(patients -> {
                    model.addAttribute("patients", patients);
                    return "index";
 	            })
 	            .onErrorResume(e -> {
 	                log.error("Erreur lors de la récupération du patient", e);
 	               return webClient.get()
 	       	            .uri("/patients")
 	       	            .header("Authorization", request.getHeader("Authorization"))
 	       	            .cookie("JWT", request.getHeader("Authorization").replace("Bearer ", ""))
 	       	            .retrieve()
 	       	            .bodyToMono(PatientDto[].class)
 	       	            .map(patients -> {
 	                          model.addAttribute("patients", patients);
 	                          return "index";
 	       	            });
 	            });

    }
    
    // Sauvegarde ou mise à jour du patient
    @PostMapping("/save")
    public Mono<String> savePatient(@ModelAttribute PatientDto patientDto, Model model,  HttpServletRequest request) {
    	
    	String jwtFromHeader = null;
 	    String jwtFromCookie = null;
 	    String jwt = null;

 	    if (request.getHeader("Authorization") != null)
 	        jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

 	    Cookie[] cookies = request.getCookies();
 	    for (Cookie cookie: cookies) {
 	        if (cookie.getName().equals("JWT")) {
 	            jwtFromCookie = cookie.getValue();
 	        }
 	    }
 	    log.info("JWT from Header: {}", jwtFromHeader);
 	    log.info("JWT from Cookie: {}", jwtFromCookie);
 	    if (jwtFromCookie != null)
 	        jwt = jwtFromCookie;
 	    else if (jwtFromHeader != null)
 	        jwt = jwtFromHeader;

 	    if (jwt == null && jwtFromCookie == null && jwt == null) {
 	        log.info("No auth available");
 	        return Mono.just("login");
 	    }

 	    log.info("JPOST WT in patient controller /save/{id} : {}", jwt);
 	    return webClient.post()
 	            .uri("/patients")
                .bodyValue(patientDto)
 	            .header("Authorization", "Bearer " + jwt)
 	            .cookie("JWT", jwt)
 	            .retrieve()
 	            .bodyToMono(PatientDto[].class)
 	            .map(patients -> {
                    model.addAttribute("patients", patients);
                    return "index";
 	            })
 	            .onErrorResume(e -> {
 	                log.error("Erreur lors de la récupération du patient", e);
	 	               model.addAttribute("patient", new PatientDto());
	 	              return Mono.just("patient-form");
 	            });

    }
    
}
    
