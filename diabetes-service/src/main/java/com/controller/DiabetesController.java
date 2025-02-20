package com.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

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

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/risks")
public class DiabetesController {

	private final WebClient webClient = WebClient.create();
    private final DiabetesService diabetesSvc;
    
    public DiabetesController(DiabetesService diabetesSvc) {
    	this.diabetesSvc = diabetesSvc;
    }

    @GetMapping("/diabetes/{patientId}")
    public ResponseEntity<Mono<PatientRiskDto>> getPatientRisk(@PathVariable("patientId") String patientId) {

	    Mono<PatientRiskDto> risk = 
	    		webClient.get().uri("http://localhost:800/patients/{id}", patientId)
	    		.exchangeToMono(response -> {
	    			return response.bodyToMono(PatientDto.class).flatMap(patient -> {
                        return webClient.post().uri("http://localhost:8083/notes/triggers/{patientId}", patientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(Arrays.stream(Trigger.values()).map(Trigger::toString).collect(Collectors.toList())))
                            .exchangeToMono(noteResponse -> {
                                return noteResponse.bodyToMono(PatientRiskDto.class)
                                    .flatMap(riskDto -> {
                                        PatientRiskDto evaluatedRisk = diabetesSvc.evaluateDiabetesRisk(patient, Integer.parseInt(patientId));
                                        return Mono.just(evaluatedRisk);
                                    });
                            });
                    });
            });

        return ResponseEntity.ok(risk);
    }
}