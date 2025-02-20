package com.service.mapper;

import com.service.dto.PatientDto;
import com.service.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    // Convertir un PatientDto en Patient
    Patient toPatient(PatientDto patientDto);

    // Convertir un Patient en PatientDto
    PatientDto toPatientDto(Patient patient);
}
