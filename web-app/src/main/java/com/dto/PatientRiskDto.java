package com.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PatientRiskDto {

    private String patientId; 
    private String risk;
    private Integer count;
}

