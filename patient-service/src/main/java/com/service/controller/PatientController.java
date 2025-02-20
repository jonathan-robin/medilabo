package com.service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.model.Patient;
import com.service.service.PatientService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<Patient>> getPatients(HttpSession session) throws Exception {
    	log.debug("Call GET /patients: {}");
        List<Patient> patients = patientService.findAll();
        if (patients.size() > 0)
        	return ResponseEntity.ok(patients); 
        else 
        	return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
    	log.debug("Call GET /patients/{}", id);
        Patient patient = patientService.findPatientById(id);
        if (patient != null)
            return new ResponseEntity<>(patient, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient newPatient = patientService.savePatient(patient);
        return new ResponseEntity<>(newPatient, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient updatedPatient = patientService.updatePatient(id, patient);
        if (updatedPatient != null) 
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  
    
}
