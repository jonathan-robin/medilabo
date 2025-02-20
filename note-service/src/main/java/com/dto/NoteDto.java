package com.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDto {

    // validation @Null to not validation a postmapping with a id 
    @NotNull(message="you don't need to post a id")
    private String id;

    @NotNull(message = "date must be not null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String date;

    @NotBlank(message = "content must be not null or blank")
    private String content;

    @Valid
    private PatientDto patient;
}