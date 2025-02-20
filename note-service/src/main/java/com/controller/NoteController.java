package com.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dto.NoteDto;
import com.model.Note;
import com.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/notes")
public class NoteController {

    private final NoteService noteService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Flux<Note>> findAll() {
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Note>> findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(noteService.findById(id)); 
    }

    @PostMapping
    public ResponseEntity<Mono<Note>> createNote(@Valid @RequestBody NoteDto noteToCreate) {
        return ResponseEntity.ok(noteService.saveNote(modelMapper.map(noteToCreate, Note.class)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Mono<Note>> updateNote(@Valid @RequestBody NoteDto noteUpdated, @PathVariable("id") String id) {
        return ResponseEntity.ok(noteService.updateNote(modelMapper.map(noteUpdated, Note.class)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteNote(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(noteService.deleteNote(id));

    }

   

}