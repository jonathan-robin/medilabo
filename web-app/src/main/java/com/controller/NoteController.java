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

    @Autowired
    public NoteController(WebClient webClient) {
        this.webClient = webClient;
    }
  
    
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
    }
 
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
 		                    .bodyToMono(PatientDto.class)
 		                    .flatMap(patient -> {
 		                        return webClient.get()
 		                                .uri("/notes/patient/" + patientId)
 		                                .header("Authorization", "Bearer " + _jwt)
 		                                .cookie("JWT", _jwt)
 		                                .retrieve()
 		                                .bodyToFlux(NoteDto.class)
 		                                .collectList() 
 		                                .map(notes -> {
 		                                    log.info("Toutes les notes : {}", notes);
 		                                    model.addAttribute("patient", patient); 
 		                                    model.addAttribute("notes", notes);
 		                                    return "patient-details";
 		                                });
 		                    })
 		        )
 		        .onErrorResume(e -> {
 		            log.error("Erreur lors de l'exécution des requêtes", e);
 		            model.addAttribute("userCredential", new Credentials());
 		            return Mono.just("patient-details");
 		        });


    }
    
    @GetMapping("/{patientId}/add")
    public String showCreateNoteForm(@PathVariable("patientId") String patientId, Model model) {
    	 NoteDto note = new NoteDto(); // Crée un nouvel objet NoteDto sans ID
         note.setPatientId(patientId); // Associer la note au patient
         model.addAttribute("note", note);
         note.setContent("");
         model.addAttribute("patientId", patientId); // Passer l'ID du patient au template

         return "notes/add-form"; // Nom du templ
    }

    @PostMapping("/{patientId}")
    public Mono<String> saveNote(@PathVariable("patientId") String patientId, @ModelAttribute("note") NoteDto noteDto, Model model, HttpServletRequest request) {
    	
    	log.info("note: {}", noteDto);
    	
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

          /******************************************************************/
          /******************************************************************/
          /********************* TODO ***********************************/
          /*
           */
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
    	                    .header("Authorization", "Bearer " + _jwt)
    	                    .cookie("JWT", _jwt)
    	                    .retrieve()
    	                    .bodyToMono(PatientDto.class)
    	                    .zipWith(Mono.just(notes));
   	            }).flatMap(result -> {
	   	             List<NoteDto> notes = result.getT2();
	   	             PatientDto patient = result.getT1(); 
		   	         model.addAttribute("notes", notes);
		   	         model.addAttribute("patient", patient);
		   	         return Mono.just("patient-details");
   	            })
   	            .onErrorResume(e -> {
   	                 log.error("Erreur lors de l'update de la note", e);
  	 	               model.addAttribute("note", new NoteDto());
  	 	              return Mono.just("notes/edit-form");
   	            });
   	 /******************************************************************/
   	 /******************************************************************/

    }

    
}
    
