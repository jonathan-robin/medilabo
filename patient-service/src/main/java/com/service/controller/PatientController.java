package com.service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.model.Patient;
import com.service.service.PatientService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<Patient>> getPatients() {
    	
    	log.debug("Call GET /patients: {}");
        List<Patient> patients = patientService.findAll();
        return patients.size() > 0 ? ResponseEntity.ok(patients) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Patient patient = patientService.findPatientById(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }
}
