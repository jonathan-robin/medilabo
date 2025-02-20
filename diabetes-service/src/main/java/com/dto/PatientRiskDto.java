package com.dto;

import java.util.Map;

import com.enums.Risk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PatientRiskDto {

    private String patientId; 
    private Map<String, Integer> triggers;
    private Risk risk;
    private Integer count;
}

