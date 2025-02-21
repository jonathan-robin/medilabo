package com.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@Document(collection = "notes")
public class Note {

    @Id
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
      
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    private String content;
    
    private String patientId;

    public Note(String id, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, String content, String patientId) {
        this.id = id;
        this.content = content;
        this.patientId = patientId;
        this.lastUpdatedAt = lastUpdatedAt;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }
}
