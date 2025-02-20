package com.dto;

import com.enums.Risk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PatientRiskDto {

	private String patientId; 
	private Risk risk; 
	private Integer count;
	
}
