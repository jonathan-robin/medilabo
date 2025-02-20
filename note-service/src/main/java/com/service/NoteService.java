package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Note;
import com.repository.NoteRepository;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {
	
    private final ObjectMapper objectMapper;
    
    @Autowired
    private NoteRepository noteRepo;
    
    public Flux<Note> findAll(){
    	return noteRepo.findAll();
    }
    
    public Mono<Note> findById(String id) {
    	log.info("Find note with id : {}, id");
    	return noteRepo.findById(id);
    }
    
    public Mono<Note> updateNote(Note note){
    	log.info("Update note with {}...", note);
    	return noteRepo.save(note);
    }
    
    public Mono<List<Note>> saveNote(Note note) {
    	log.info("Saving note with...");
    	return noteRepo.save(note).flatMap(n -> noteRepo.findByPatientId(note.getPatientId()).collectList());
    }
    
    public Mono<Void> deleteNote(String id){ 
    	log.info("Saving note with id {}...", id);
    	return noteRepo.deleteById(id);
    }
    
    public Flux<Note> findByPatientId(String id){ 
    	log.info("Find note with patient id {}...", id);
    	return noteRepo.findByPatientId(id);
    }
    
    public Mono<Note> updateNote(String content, String id) {
        log.info("Updating note with id {} with new comment {}", id, content);
        return noteRepo.findById(id)
            .flatMap(note -> {
                note.setContent(content);
                note.setLastUpdatedAt(LocalDateTime.now());
                return noteRepo.save(note);
            })
            .switchIfEmpty(Mono.error(new RuntimeException("Note not found")));
    }


    
	
    /*********** Import note from json file ***********/
    public void importAll(InputStream jsonStream) throws StreamReadException, DatabindException, IOException {

    	objectMapper.readValue(jsonStream, new TypeReference<List<Note>>(){})
			.stream().forEach(noteJson -> {
				Note note = new Note(noteJson.getId(), noteJson.getCreatedAt(), noteJson.getLastUpdatedAt(), noteJson.getContent(), noteJson.getPatientId());
				saveNote(note).subscribe();
			});
    }
    

    
    /*****************************************************/
	
	
}
