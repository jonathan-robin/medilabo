package com.service;

import java.io.IOException;
import java.io.InputStream;

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
    
    public Mono<Note> saveNote(Note note) {
    	log.info("Saving note ...{}", note.toString());
        return noteRepo.save(note);
    }
    
    public Mono<Void> deleteNote(String id){ 
    	log.info("Saving note with id {}...", id);
    	return noteRepo.deleteById(id);
    }
    
    public Flux<Note> findByPatientId(String id){ 
    	log.info("Find note with patient id {}...", id);
    	return noteRepo.findByPatientId(id);
    }
    
    public Mono<Note> updateNote(String content, String id){ 
    	log.info("Updating note with id {} with new comment {}", id, content);
    	return noteRepo.findById(id)
    	        .map(n -> {
    	            n.setContent(content);
    	            return noteRepo.save(n);  
    	        })
    	        .flatMap(savedNoteMono -> savedNoteMono)
    	        .doOnSuccess(updatedNote -> log.info("Note updated successfully: {}", updatedNote))
    	        .doOnError(error -> log.error("Error updating note", error));
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
