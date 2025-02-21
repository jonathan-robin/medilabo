package com.service;
import java.time.LocalDate;
import java.time.Period;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dto.PatientDto;
import com.dto.PatientRiskDto;
import com.enums.Risk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@Getter
@RequiredArgsConstructor
public class DiabetesService {

    public Risk computeRisk(Integer age, Integer triggerCount, String gender) { 
    	
        if (triggerCount == 0) {
            return Risk.NONE;
        } else if (triggerCount >= 2 && triggerCount <= 5 && age > 30) {
            return Risk.BORDERLINE;
        } else if (triggerCount >= 3 && gender.equalsIgnoreCase("M") && age < 30) {
            return Risk.INDANGER;
        } else if (triggerCount >= 4 && gender.equalsIgnoreCase("F") && age < 30) {
            return Risk.INDANGER;
        } else if (age > 30 && (triggerCount == 6 || triggerCount == 7)) {
            return Risk.INDANGER; 
        } else if (triggerCount >= 5 && gender.equalsIgnoreCase("M") && age < 30) {
            return Risk.EARLYONSET;
        } else if (triggerCount >= 7 && gender.equalsIgnoreCase("F") && age < 30) {
            return Risk.EARLYONSET;
        } else if (age > 30 && triggerCount >= 8) {
            return Risk.EARLYONSET;
        }
        	return Risk.empty;
    }
    
    public PatientRiskDto evaluateDiabetesRisk(PatientDto patient, Integer triggerCount) {

    	Integer age = Period.between(LocalDate.parse(patient.getBirthDate()), LocalDate.now()).getYears();
    	PatientRiskDto risk = new PatientRiskDto();
    	risk.setRisk(computeRisk(age, triggerCount, patient.getGender()));
    	risk.setPatientId(patient.getId().toString());
    	risk.setCount(triggerCount);
    	return risk;
    	
    } 

}