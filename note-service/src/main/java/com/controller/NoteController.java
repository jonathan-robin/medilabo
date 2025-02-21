package com.controller;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
		log.info("Call /id with {} - find by id ", id); 
		log.info("Result:  {}", noteService.findById(id)); 
        return noteService.findById(id)
        .map(note -> new ResponseEntity<Note>(note, HttpStatus.FOUND))
        .switchIfEmpty(Mono.error(new Exception("No note found")));
    }

    @PostMapping("")
    public ResponseEntity<Mono<List<Note>>> createNote(@Valid @RequestBody Note noteToCreate) {
    	log.info("Call POST / create Note : {} {} {} {} {}", noteToCreate.getContent(), noteToCreate.getId(), noteToCreate.getCreatedAt(), noteToCreate.getLastUpdatedAt(), noteToCreate.getPatientId());
        return ResponseEntity.ok(noteService.saveNote(modelMapper.map(noteToCreate, Note.class)));
    }

    @PutMapping("/{id}")
    public Mono<Note> updateNote(@Valid @RequestBody String content, @PathVariable("id") String id) {
		log.info("Call PUT /id id {} - updateNote with {}", id, content); 
		log.info("Result:  {}", noteService.findById(id)); 
        return noteService.updateNote(content, id);

    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteNote(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(noteService.deleteNote(id));
    }

	@GetMapping("/patient/{id}")
    public Flux<Note> findByPatientId(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
    	
		log.info("auth type : {}", request.getAuthType()); 
		log.info("cokoie : {}", request.getCookies() != null); 
		log.info("auth type : {}", request.getHeader("Authorization")); 
		jakarta.servlet.http.Cookie cookie[] = request.getCookies();
		for (jakarta.servlet.http.Cookie cook: cookie) {
			if (cook.getName().equals("JWT")) { 
				log.info("JWT found {}", cook.getValue());
				String jwt = cook.getValue();
				response.addCookie(new jakarta.servlet.http.Cookie("JWT", jwt));
				response.addHeader("Authorization", "Bearer " + jwt);
			}
		}
		
		log.info("CALL /patient/id with id : {}", id);
    	return (Flux<Note>)noteService.findByPatientId(id);

    }
    
    @PostMapping("/triggers/{patientId}")
    public ResponseEntity<Mono<PatientRiskDto>> computeTriggers(@PathVariable("patientId") Long patientId, @RequestBody(required = true) List<String> triggers) {
    	 log.info("Reçu triggers pour patient {}: {}", patientId, triggers);
    	 if (triggers == null || triggers.isEmpty()) {
    	        log.error("La liste des triggers est vide !");
    	    }else {
    	    	
    	    	String regex = triggers.stream()
    	    			.map(Pattern::quote)
    	    			.collect(Collectors.joining("|")); // Générer la regex
    	    	log.info("Regex construite : {}", regex);
	    	return ResponseEntity.ok(noteService.computeTriggers(patientId, regex));
    	    }
    	 return ResponseEntity.ok(Mono.empty());
    }


}