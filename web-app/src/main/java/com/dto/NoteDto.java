package com.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDto {

    
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
      
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    private String content;
    
    private String patientId;

    public NoteDto(String id, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, String content, String patientId) {
        this.id = id;
        this.content = content;
        this.patientId = patientId;
        this.lastUpdatedAt = lastUpdatedAt;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }
}