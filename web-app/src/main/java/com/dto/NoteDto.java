package com.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private String id;

    @NotNull(message = "date must be not null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String date;

    @NotBlank(message = "content must be not null or blank")
    private String content;

    @Valid
    private String patientId;

    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
      
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;


    public NoteDto(String id, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, String content, String patientId) {
        this.id = id;
        this.content = content;
        this.patientId = patientId;
        this.lastUpdatedAt = lastUpdatedAt != null ? lastUpdatedAt : null;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }
}