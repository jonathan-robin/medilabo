package com.controller;

import java.util.Arrays;
import java.util.List;

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

import com.dto.NoteDto;
import com.dto.PatientDto;
import com.model.Credentials;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NoteController {

    private final WebClient webClient;

    @Autowired
    public NoteController(WebClient webClient) {
        this.webClient = webClient;
    }
 
    
    
    @GetMapping("edit/{id}")
    public Mono<String> showUpdateForm(@PathVariable String id, Model model, HttpServletRequest request, HttpServletResponse response) {

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

    	    final String _jwt = jwt;
    	    
    	    return webClient.get()
	            .uri("/notes/" + id)
	            .header("Authorization", "Bearer " + jwt)
	            .cookie("JWT", jwt)
	            .retrieve()
	            .bodyToMono(NoteDto.class)
	            .flatMap(note -> {
	            	
	            	model.addAttribute("note", note);
	            	return Mono.just("notes/edit-form");

	            });

    }
    

	@PostMapping("/{patientId}/edit/{id}/")
    public Mono<String> editNote(@ModelAttribute("note") NoteDto noteDto, @PathVariable Integer patientId, @PathVariable String id, Model model, HttpServletRequest request) {
        
    	log.info("note: {}", noteDto.toString());
    	
        String jwtFromHeader = null;
        String jwtFromCookie = null;
        String jwt = null;

        if (request.getHeader("Authorization") != null)
            jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                jwtFromCookie = cookie.getValue();
            }
        }
        
        jwt = (jwtFromCookie != null) ? jwtFromCookie : jwtFromHeader;

        if (jwt == null)
            return Mono.just("redirect:/login");
        
        final String _jwt = jwt;
        
        return webClient.get()
        	    .uri("/patients/" + patientId)
        	    .header("Authorization", "Bearer " + jwt)
        	    .cookie("JWT", jwt)
        	    .retrieve()
        	    .bodyToMono(PatientDto.class) 
        	    .flatMap(patient -> {
        	        log.info("Patient récupéré : {}", patient);

        	        return webClient.put()
        	            .uri("/notes/" + id)
        	            .bodyValue(noteDto.getContent())
        	            .header("Authorization", "Bearer " + _jwt)
        	            .cookie("JWT", _jwt)
        	            .retrieve()
        	            .bodyToMono(NoteDto.class)
        	            .flatMap(updatedNote -> {
        	                log.info("Note mise à jour : {}", updatedNote);

        	                // 3ème requête : récupérer toutes les notes associées au patient
        	                return webClient.get()
        	                    .uri("/notes/patient/" + patientId)
        	                    .header("Authorization", "Bearer " + _jwt)
        	                    .cookie("JWT", _jwt)
        	                    .retrieve()
        	                    .bodyToFlux(NoteDto.class)
        	                    .collectList()  // Les collecter sous forme de liste
        	                    .doOnNext(notes -> log.info("Détails notes : {}", notes))
        	                    .map(notes -> {
        	                        model.addAttribute("patient", patient);
        	                        model.addAttribute("notes", notes);
        	                        return "patient-details";
        	                    });
        	            });
        	    })
        	    .onErrorResume(e -> {
        	        log.error("Erreur lors de l'exécution des requêtes", e);
        	        model.addAttribute("userCredential", new Credentials());
        	        return Mono.just("patient-details");  // Retourner une vue d'erreur
        	    });

        
//        return webClient.put()
//                .uri("/notes/" + id)
//                .bodyValue(noteDto.getContent())
//                .header("Authorization", "Bearer " + jwt)
//                .cookie("JWT", jwt)
//                .retrieve()
//                .bodyToFlux(NoteDto.class)
//                .flatMap(notes -> {   
//                    return webClient.get()
//                        .uri("/patients/" + patientId)
//                        .header("Authorization", "Bearer " + _jwt)
//                        .cookie("JWT", _jwt)
//                        .retrieve()
//                        .bodyToMono(PatientDto.class)
//                        .map(patient -> {  
//                            log.info("patient in update: {}", patient.toString());
//                            model.addAttribute("patient", patient);
//                            log.info("notes in update: {}", notes.toString());
//                            model.addAttribute("notes", notes);  // Ajouter la liste de notes au modèle
//                            return Mono.just("patient-details");  // Retourner la vue
//                        });
//                })
//                .onErrorResume(e -> {
//                    log.error("Erreur lors de la modification de la note", e);
//                    model.addAttribute("userCredential", new Credentials());
//                    return Flux.just(Mono.just("patient-details"));
// // Retourner la vue d'erreur
//                });

    }

    
    // Sauvegarde ou mise à jour du patient
    @GetMapping("/{patientId}/delete/{id}")
    public Mono<String> deleteNote(@PathVariable String id, Model model, @PathVariable String patientId, HttpServletRequest request) {
    	
    	
        String jwtFromHeader = null;
        String jwtFromCookie = null;
        String jwt = null;

        if (request.getHeader("Authorization") != null)
            jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                jwtFromCookie = cookie.getValue();
            }
        }
        
        jwt = (jwtFromCookie != null) ? jwtFromCookie : jwtFromHeader;

        if (jwt == null)
            return Mono.just("redirect:/login");
        
        final String _jwt = jwt;

 	   return webClient.delete()
 		        .uri("/notes/" + id)
 		        .header("Authorization", "Bearer " + jwt)
 		        .cookie("JWT", jwt)
 		        .retrieve()
 		        .bodyToMono(Void.class)
 		        .then(
 		            webClient.get()
 		                    .uri("/patients/" + patientId)
 		                    .header("Authorization", "Bearer " + jwt)
 		                    .cookie("JWT", jwt)
 		                    .retrieve()
 		                    .bodyToMono(PatientDto.class)  // Récupérer le patient
 		                    .flatMap(patient -> {
 		                        return webClient.get()
 		                                .uri("/notes/patient/" + patientId)
 		                                .header("Authorization", "Bearer " + _jwt)
 		                                .cookie("JWT", _jwt)
 		                                .retrieve()
 		                                .bodyToFlux(NoteDto.class)  // Récupérer toutes les notes du patient
 		                                .collectList()  // Collecter sous forme de liste
 		                                .map(notes -> {
 		                                    log.info("Toutes les notes : {}", notes);
 		                                    model.addAttribute("patient", patient);  // Ajouter le patient au modèle
 		                                    model.addAttribute("notes", notes);  // Ajouter les notes au modèle
 		                                    return "patient-details";  // Retourner la vue
 		                                });
 		                    })
 		        )
 		        .onErrorResume(e -> {
 		            log.error("Erreur lors de l'exécution des requêtes", e);
 		            model.addAttribute("userCredential", new Credentials());
 		            return Mono.just("patient-details");  // Retourner une vue d'erreur
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
    
