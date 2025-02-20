package com.service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service.model.Patient;
import com.service.repository.PatientRepository;

@Service
public class PatientService {
	
	private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    
    public List<Patient> findAll() {
        return patientRepository.findAll(); 
    }

    public Patient findPatientById(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return patient.orElse(null);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patient) {
        if (patientRepository.existsById(id)) {
            patient.setId(id);
            return patientRepository.save(patient); 
        }
        return null;  
    }
    
    public void deletePatientById(Long id) {
        if (patientRepository.findById(id) != null)
        	patientRepository.deleteById(id);
    }


}
