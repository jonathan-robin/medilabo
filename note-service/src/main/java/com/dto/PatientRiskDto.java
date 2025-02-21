package com.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import com.enums.Risk;

@Getter
@Setter
@NoArgsConstructor
public class PatientRiskDto {

    private String patientId; 
    private Risk risk;
    private Integer count;
}
