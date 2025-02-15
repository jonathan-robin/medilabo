package com.service.service;

import java.util.List;

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

    public Patient findPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }
    
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

}
