package com.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.dto.NoteDto;
import com.dto.PatientDto;
import com.dto.PatientRiskDto;
import com.model.Credentials;
import com.service.CookieService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NoteController {

    private final WebClient webClient;
    private final CookieService cookieService;
    
    @Autowired
    private PatientController patientController;

    @Autowired
    public NoteController(WebClient webClient, CookieService cookieService) {
        this.webClient = webClient;
        this.cookieService = cookieService;
    }
  
    /* affiche le formulaire pour edit une note */
    @GetMapping("edit/{id}")
    public Mono<String> showNote(@PathVariable String id, Model model, HttpServletRequest request, HttpServletResponse response) {

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

    /* push avec les modifs pour une note */
	@PostMapping("/{patientId}/edit/{id}/")
    public Mono<Mono<String>> editNote(@ModelAttribute("note") NoteDto noteDto, @PathVariable Integer patientId, @PathVariable String id, Model model, HttpServletRequest request) {
        
   	 final String jwt = cookieService.getCookie(request);
	 
   	 if (jwt == null)
   		 return Mono.just(Mono.just("login"));
        
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
        	            .header("Authorization", "Bearer " + jwt)
        	            .cookie("JWT", jwt)
        	            .retrieve()
        	            .bodyToMono(NoteDto.class)
        	            .flatMap(updatedNote -> {
           	                return webClient.get()
        	                    .uri("/notes/patient/" + patientId)
        	                    .header("Authorization", "Bearer " + jwt)
        	                    .cookie("JWT", jwt)
        	                    .retrieve()
        	                    .bodyToFlux(NoteDto.class)
        	                    .collectList()
        	                    .map(notes -> {
        	                        model.addAttribute("patient", patient);
        	                        model.addAttribute("notes", notes);
        	                        return patientController.getPatientRisk(patient.getId().toString(), model, jwt).flatMap(c -> Mono.just(c));
        	                    });
        	            });
        	    })
        	    .onErrorResume(e -> {
        	        log.error("Erreur lors de l'exécution des requêtes", e);
        	        model.addAttribute("userCredential", new Credentials());
        	        return Mono.just(Mono.just("patient-details"));
        	    });
    }
 
	/* DELETE UNE NOTE EN PASASNT EN ID **/    @GetMapping("/{patientId}/delete/{id}")
    public Mono<Mono<String>> deleteNote(@PathVariable String id, Model model, @PathVariable String patientId, HttpServletRequest request) {

   	 final String jwt = cookieService.getCookie(request);
	 
   	 if (jwt == null)
   		 return Mono.just(Mono.just("/login"));
   	 
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
 		                    .bodyToMono(PatientDto.class)
 		                    .flatMap(patient -> {
 		                        return webClient.get()
 		                                .uri("/notes/patient/" + patientId)
 		                                .header("Authorization", "Bearer " + jwt)
 		                                .cookie("JWT", jwt)
 		                                .retrieve()
 		                                .bodyToFlux(NoteDto.class)
 		                                .collectList() 
 		                                .map(notes -> {
 		                                    model.addAttribute("patient", patient); 
 		                                    model.addAttribute("notes", notes);
 		                                    return patientController.getPatientRisk(patientId, model, jwt);
 		                                });
 		                    })
 		        )
 		        .onErrorResume(e -> {
 		            log.error("Erreur lors de l'exécution des requêtes", e);
 		            model.addAttribute("userCredential", new Credentials());
 		            return Mono.just(Mono.just("patient-details"));
 		        });


    }
    
	/* GET patient ID to sho form to add a note */
    @GetMapping("/{patientId}/add")
    public String showCreateNoteForm(@PathVariable("patientId") String patientId, Model model) {
    	 NoteDto note = new NoteDto(); // Crée un nouvel objet NoteDto sans ID
         note.setPatientId(patientId); // Associer la note au patient
         model.addAttribute("note", note);
         note.setContent("");
         model.addAttribute("patientId", patientId); // Passer l'ID du patient au template

         return "notes/add-form"; // Nom du templ
    }

    /* POST pôur sauver une note en passant dto */
    @PostMapping("/{patientId}")
    public Mono<String> saveNote(@PathVariable("patientId") String patientId, @ModelAttribute("note") NoteDto noteDto, Model model, HttpServletRequest request) {
    	
    	 final String jwt = cookieService.getCookie(request);
    	 
    	 if (jwt == null)
    		 return Mono.just("/login");
    	 
   	    return webClient.post()
   	            .uri("/notes")
                .bodyValue(noteDto)
   	            .header("Authorization", "Bearer " + jwt)
   	            .cookie("JWT", jwt)
   	            .retrieve()
   	            .bodyToMono(List.class)
   	            .flatMap(notes -> {
   	            	return webClient.get()
    	                    .uri("patients/" + patientId)
    	                    .header("Authorization", "Bearer " + jwt)
    	                    .cookie("JWT", jwt)
    	                    .retrieve()
    	                    .bodyToMono(PatientDto.class)
    	                    .zipWith(Mono.just(notes));
   	            }).flatMap(result -> {
	   	             List<NoteDto> notes = result.getT2();
	   	             PatientDto patient = result.getT1(); 
		   	         model.addAttribute("notes", notes);
		   	         model.addAttribute("patient", patient);
		   	         return patientController.getPatientRisk(patientId, model, jwt);
   	            })
   	            .onErrorResume(e -> {
   	                 log.error("Erreur lors de l'update de la note", e);
  	 	               model.addAttribute("note", new NoteDto());
  	 	              return Mono.just("notes/edit-form");
   	            });
    	}
}
    
