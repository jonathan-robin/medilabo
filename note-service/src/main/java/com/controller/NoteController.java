package com.controller;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.PatientRiskDto;
import com.model.Note;
import com.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/notes")
public class NoteController {

    private final NoteService noteService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<Flux<Note>> findAll() {
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Note>> findById(@PathVariable("id") String id) {
        return noteService.findById(id)
        .map(note -> new ResponseEntity<Note>(note, HttpStatus.FOUND))
        .switchIfEmpty(Mono.error(new Exception("No note found")));
    }

    @PostMapping("")
    public ResponseEntity<Mono<List<Note>>> createNote(@Valid @RequestBody Note noteToCreate) {
    	log.info("Call to controller create Note...");
    	log.info("noteToCreate : {} {} {} {} {}", noteToCreate.getContent(), noteToCreate.getId(), noteToCreate.getCreatedAt(), noteToCreate.getLastUpdatedAt(), noteToCreate.getPatientId());
        return ResponseEntity.ok(noteService.saveNote(modelMapper.map(noteToCreate, Note.class)));
    }

    @PutMapping("/{id}")
    public Mono<Note> updateNote(@Valid @RequestBody String content, @PathVariable("id") String id) {
        return noteService.updateNote(content, id);

    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteNote(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(noteService.deleteNote(id));
    }

    @SuppressWarnings("unchecked")
	@GetMapping("/patient/{id}")
    public Flux<Note> findByPatientId(@PathVariable("id") String id) {
    	log.info("CALL /patient/id with id : {}", id);
    	return (Flux<Note>)noteService.findByPatientId(id);

    }
    
    @PostMapping("/triggers/{patientId}")
    public ResponseEntity<Mono<PatientRiskDto>> computeTriggers(@PathVariable("patientId") Long patientId, @RequestBody List<String> triggers) {
    	return ResponseEntity.ok(noteService.computeTriggers(patientId, triggers));
//                .map(sum -> modelMapper.map(sum, SumTermTriggersDto.class)));
    }


}