package com.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
	
	
}