package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dto.NoteDto;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Note;
import com.repository.NoteRepository;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {
	
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private NoteRepository noteRepo;
	
    public void importAll(InputStream jsonStream) throws StreamReadException, DatabindException, IOException {

    	objectMapper.readValue(jsonStream, new TypeReference<List<NoteDto>>(){})
			.stream().forEach(note -> {
				log.info("Saving note ...{}");
				saveNote(modelMapper.map(note, Note.class)).subscribe();
			});
    }
    
    public Mono<Note> saveNote(Note note) {
        return noteRepo.save(note);
    }
	
	
}
