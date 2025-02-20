package com.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.model.Note;

import reactor.core.publisher.Flux;



@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    
	 public Flux<Note> findByPatientId(String id);
	
}