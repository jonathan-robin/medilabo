package com.controller;

import java.util.Arrays;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/patients")
@Slf4j
public class PatientController {

    private final WebClient webClient;
    private final CookieService cookieService;

    @Autowired
    public PatientController(WebClient webClient, CookieService cookieService) {
        this.webClient = webClient;
        this.cookieService = cookieService;
    }
    
    @GetMapping("")
    public Mono<String> getAllPatients(Model model, HttpServletRequest request, HttpServletResponse response) {
    
    	final String jwt = cookieService.getCookie(request);
      	 
	   	 if (jwt == null)
	   		 return Mono.just("login");
	   	 
        return webClient.get()
                .uri("/patients")
                .header("Authorization", "Bearer " +  jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientDto[].class)
                .map(Arrays::asList)
                .map(patients -> {
                	if (patients != Mono.just(null))
                		model.addAttribute("patients", patients);
                	response.setHeader("Authorization", "Bearer " + jwt);
                    return "index";
                })
                .onErrorResume(e -> {
                    log.error("Erreur lors de la récupération des patients", e);
                    return Mono.just("home");
                });
    }
    
    @GetMapping("/{id}")
    public Mono<String> getPatientDetails(@PathVariable String id, Model model, HttpServletRequest request) {

    	final String jwt = cookieService.getCookie(request);
   	 
	   	 if (jwt == null)
	   		 return Mono.just("login");
                
        return webClient.get()
                .uri("/patients/" + id)
                .header("Authorization", "Bearer " + jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientDto.class)
                .flatMap(patient -> {
                    return webClient.get()
                            .uri("/notes/patient/" + id)
                            .header("Authorization", "Bearer " + jwt)
                            .cookie("JWT", jwt)
                            .retrieve()
                            .bodyToFlux(NoteDto.class)
                            .collectList()
                            .doOnNext(notes -> log.info("Détails note : {}", notes.toString()))
                            .flatMap(notes -> { 
                            	notes.forEach(n -> log.info(n.toString()));
                                model.addAttribute("patient", patient);
                                model.addAttribute("notes", notes);
                                return (Mono<String>)getPatientRisk(patient.getId().toString(), model, jwt);
                }).onErrorResume(e -> {
 	                log.error("Erreur lors de la récupération du patient", e);
 	                return Mono.just("/patients");
 	            });
         });

    }

    public Mono<String> getPatientRisk(String patientId, Model model, String jwt) {
    	
        return webClient.get()
                .uri("/diabetes/" + patientId)
                .header("Authorization", "Bearer " + jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientRiskDto.class)
                .doOnSuccess(risk -> log.info("Réponse du diabète reçue: {}, {}, {}, {}", risk.getCount(), risk.getPatientId(), risk.getRisk()))
                .doOnError(error -> log.error("Erreur lors de l'appel à /diabetes/{}: {}", patientId, error.getMessage()))
                .flatMap(risk -> {
                    log.info("Risk: {}", risk);
                    model.addAttribute("patientRisk", risk);
                    return Mono.just("patient-details");
                })
                .onErrorResume(error -> {
                    return Mono.empty();
                });
    }

    @GetMapping("/new")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "patient-form";
    }
    
    @GetMapping("edit/{id}")
    public Mono<String> showUpdateForm(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {

    		final String jwt = cookieService.getCookie(request); 
    		
    		if (jwt == null)
    			return Mono.just("/login");
    	    
    	    return webClient.get()
	            .uri("/patients/" + id)
	            .header("Authorization", "Bearer " + jwt)
	            .cookie("JWT", jwt)
	            .retrieve()
	            .bodyToMono(PatientDto.class)
	            .flatMap(patient -> {
	            	return webClient.get()
		            	.uri("/notes/patient/"+id)
		            	.header("Authorization", "Bearer " + jwt)
	    	            .cookie("JWT", jwt)
	    	            .retrieve()
	    	            .bodyToFlux(NoteDto.class)
	    	            .collectList()
	    	            .map(notes -> {	    	            	
	    	            	model.addAttribute("patient", patient);
	    	            	model.addAttribute("notes", notes);
	    	            	return "patient-edit-form";
	    	            });
	            });
    }
    

    @PostMapping("/edit/{id}")
    public Mono<Mono<String>> editPatient(@ModelAttribute("patient") PatientDto patientDto, @PathVariable Long id, Model model,  HttpServletRequest request) {
    	
		final String jwt = cookieService.getCookie(request); 
		
		if (jwt == null)
			return Mono.just(Mono.just("/login"));

 	    return webClient.put()
 	            .uri("/patients/" + id)
                .bodyValue(patientDto)
 	            .header("Authorization", "Bearer " + jwt)
 	            .cookie("JWT", jwt)
 	            .retrieve()
 	            .bodyToMono(PatientDto.class)
 	            .map(patient -> {
 	            	return getPatientDetails(patientDto.getId().toString(), model, request);
 	            })
 	            .onErrorResume(e -> {
 	                log.error("Erreur lors de la récupération du patient", e);
 	                model.addAttribute("userCredential", new Credentials());
 	                return Mono.just(Mono.just("redirect:/login"));
 	            });

    	
    	

    }
    
    @GetMapping("/delete/{id}")
    public Mono<String> deletePatient(@PathVariable Long id, Model model,  HttpServletRequest request) {
    	
		final String jwt = cookieService.getCookie(request); 
		
		if (jwt == null)
			return Mono.just("/login");


	    return webClient.delete()
	            .uri("/patients/" + id)
	            .header("Authorization", "Bearer " + jwt)
	            .cookie("JWT", jwt)
	            .retrieve()
	            .bodyToMono(Void.class) 
	            .then(
	                webClient.delete()
	                        .uri("/notes/patient/", id) 
	                        .header("Authorization", "Bearer " + jwt)
	                        .cookie("JWT", jwt)
	                        .retrieve()
	                        .bodyToMono(Void.class) 
	            )
	            .then(
	                webClient.get()
	                        .uri("/patients")
	                        .header("Authorization", request.getHeader("Authorization"))
	                        .cookie("JWT", jwt)
	                        .retrieve()
	                        .bodyToMono(PatientDto[].class)
	                        .map(patients -> {
	                            model.addAttribute("patients", patients);
	                            return "index";
	                        })
	            )
	            .onErrorResume(e -> {
	                return webClient.get()
	                        .uri("/patients")
	                        .header("Authorization", request.getHeader("Authorization"))
	                        .cookie("JWT", jwt)
	                        .retrieve()
	                        .bodyToMono(PatientDto[].class)
	                        .map(patients -> {
	                            model.addAttribute("patients", patients);
	                            return "index";
	                        });
	            });
    }
    
    @PostMapping("/save")
    public Mono<String> savePatient(@ModelAttribute PatientDto patientDto, Model model,  HttpServletRequest request) {
    	
		final String jwt = cookieService.getCookie(request); 
		
		if (jwt == null)
			return Mono.just("/login");
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
    
