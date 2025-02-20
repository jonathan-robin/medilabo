package com.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.model.Note;




@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    
	 public List<Note> findByPatientId(String id);
	
}