package com.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.dto.PatientDto;
import com.dto.PatientRiskDto;
import com.enums.Trigger;
import com.service.DiabetesService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(value = "/diabetes")
public class DiabetesController {

	private final WebClient webClient = WebClient.create();
    private final DiabetesService diabetesSvc;
    
    public DiabetesController(DiabetesService diabetesSvc) {
    	this.diabetesSvc = diabetesSvc;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Mono<PatientRiskDto>> getPatientRisk(@PathVariable("patientId") String patientId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        log.info("JWT reçu: {}", jwt);

        if (jwt == null || !jwt.startsWith("Bearer ")) {
            log.error("JWT manquant ou incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String jwtToken = jwt.replace("Bearer ", "");
        log.info("Token JWT nettoyé: {}", jwtToken);
        
        log.info("Appel GET /diabetes/{}", patientId);

        // Récupération du patient
        Mono<PatientRiskDto> risk = webClient.get()
            .uri("http://localhost:8080/patients/{id}", patientId)
            .header("Authorization", "Bearer " + jwtToken)  // Ajouter le JWT
            .retrieve()
            .bodyToMono(PatientDto.class)
            .flatMap(patient -> {
                log.info("Patient récupéré: {}", patient);
                List<String> triggerList = Arrays.stream(Trigger.values())
                        .map(Trigger::toString)
                        .collect(Collectors.toList());

                log.info("Envoi de la requête à /notes/triggers/{} avec triggers={}", patientId, triggerList);
                
//                values.forEach(v -> log.info("{}", v.toString()));
                
                // Récupération des déclencheurs
                return webClient.post()
                    .uri("http://localhost:8080/notes/triggers/" + patientId)
                    .header("Authorization", "Bearer " + jwtToken)  // Ajouter le JWT
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(triggerList))
                    .retrieve()
                    .bodyToMono(PatientRiskDto.class)
                    .flatMap(riskDto -> {
                        log.info("Réponse des notes: {}", riskDto);
                        // Évaluation du risque
                        PatientRiskDto evaluatedRisk = diabetesSvc.evaluateDiabetesRisk(patient, Integer.parseInt(patientId));
                        return Mono.just(evaluatedRisk);
                    })
                    .onErrorResume(error -> {
                        log.error("Erreur lors de l'appel aux notes: {}", error.getMessage());
                        return Mono.empty(); // Renvoie un Mono vide si erreur
                    });
            })
            .onErrorResume(error -> {
                log.error("Erreur lors de l'appel patient: {}", error.getMessage());
                return Mono.empty();
            });

        return ResponseEntity.ok(risk);
    }

}