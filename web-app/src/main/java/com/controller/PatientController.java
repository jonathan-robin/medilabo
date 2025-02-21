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
import com.dto.PatientRiskDto;
import com.model.Credentials;
import com.service.CookieService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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
	   	 
	   	 log.info("****[JWT]**** in PatientController.allPatients() {} *****" , jwt);
    	
        return webClient.get()
                .uri("/patients")
                .header("Authorization", "Bearer " +  jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientDto[].class)
                .map(Arrays::asList)
                .doOnNext(patients -> log.info("Patients récupérés : {}", patients))
                .map(patients -> {
//                	String _jwtFromCookie = null;
//                	for (Cookie cookie: cookies) { 
//                		if (cookie.getName().equals("JWT")) {
//                			 _jwtFromCookie = cookie.getValue();			
//                		}
//                	}
//                	Cookie cookie = new Cookie("JWT", jwt);
//                	
//                	response.addCookie(cookie);
                	response.setHeader("Authorization", "Bearer " + jwt);
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
    public Mono<String> getPatientDetails(@PathVariable String id, Model model, HttpServletRequest request) {

    	final String jwt = cookieService.getCookie(request);
   	 
	   	 if (jwt == null)
	   		 return Mono.just("login");
	   	 
	   	 log.info("****[JWT]**** in PatientController.getPatientDetails {} ************************" , jwt);
                
        return webClient.get()
                .uri("/patients/" + id)
                .header("Authorization", "Bearer " + jwt)
                .cookie("JWT", jwt)
                .retrieve()
                .bodyToMono(PatientDto.class)
                .flatMap(patient -> {
                	  log.info("****[JWT]**** inside before notes/patient/3  {} ************************" , jwt);
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
                                log.info("****[JWT]**** inside before risk {} ************************" , jwt);
                                log.info("****[JWT]**** inside before risk  {} ************************" , request.getHeader("Authorization"));
                                return (Mono<String>)getPatientRisk(patient.getId().toString(), model, jwt);
                            });
                });
    }

    public Mono<String> getPatientRisk(String patientId, Model model, String jwt) {
    	
//    	String jwt = cookieService.getCookie(request);
    	
    	log.info("****[JWT]**** in PatientController.getPatientRisk {}", jwt);
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
                    log.error("Erreur critique sur /diabetes/{}", patientId);
                    return Mono.empty(); // Empêche un crash si erreur
                });
    }

    @GetMapping("/new")
    public String showPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "patient-form";
    }
    
    @GetMapping("edit/{id}")
    public Mono<String> showUpdateForm(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {

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
	            .uri("/patients/" + id)
	            .header("Authorization", "Bearer " + jwt)
	            .cookie("JWT", jwt)
	            .retrieve()
	            .bodyToMono(PatientDto.class)
	            .flatMap(patient -> {
	            	return webClient.get()
		            	.uri("/notes/patient/"+id)
		            	.header("Authorization", "Bearer " + _jwt)
	    	            .cookie("JWT", _jwt)
	    	            .retrieve()
	    	            .bodyToFlux(NoteDto.class)
	    	            .collectList()
	    	            .doOnNext(notes -> log.info("Détails note : {}", notes.toString()))
	    	            .map(notes -> {
	    	            	
	    	            	log.info("Note: {}", notes);
	    	            	Arrays.asList(notes).forEach(n -> log.info("Note{} :", n.toString()));
	    	            	model.addAttribute("patient", patient);
	    	            	model.addAttribute("notes", notes);
	    	            	return "patient-edit-form";
	    	            });
	            });
//            .subscribe(successCallback::accept);
    	    
    	    
    	    
//    	    log.info("JWT in patient controller /edit/{id} : {}", jwt);
//    	    return webClient.get()
//    	            .uri("/patients/" + id)
//    	            .header("Authorization", "Bearer " + jwt)
//    	            .cookie("JWT", jwt)
//    	            .retrieve()
//    	            .bodyToMono(PatientDto.class)
//    	            .doOnNext(patient -> log.info("Détails patient : {}", patient))
//    	            .map(patient -> {
//                        response.addCookie(new Cookie("JWT", request.getHeader("Authorization").replace("Bearer ", ""))); 
//                        response.addHeader("Authorization", request.getHeader("Authorization"));
//    	            	model.addAttribute("patient", patient);
//    	            	
//    	            	return webClient.get()
//    	            	.uri("/notes/patient/"+id)
//    	            	.header("Authorization", "Bearer " + request.getHeader("Authorization"))
//        	            .cookie("JWT", request.getHeader("Authorization").replace("Bearer ", ""))
//        	            .retrieve()
//        	            .bodyToMono(NoteDto.class)
//        	            .doOnNext(note -> log.info("Détails note : {}", note))
//        	            .map(note -> {
//        	            	
//        	            	model.addAttribute("note", note);
//        	            	return "patient-details";
//        	            	
//        	            })
//        	            .onErrorResume(e -> {
//        	                log.error("Erreur lors de la récupération de la note", e);
//        	                model.addAttribute("userCredential", new Credentials());
//        	                return Mono.just("redirect:/login");
//        	            });   
//    	            })
//    	            .onErrorResume(e -> {
//    	                log.error("Erreur lors de la récupération du patient", e);
//    	                model.addAttribute("userCredential", new Credentials());
//    	                return Mono.just(Mono.just("redirect:/login"));
//    	            });

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
    
