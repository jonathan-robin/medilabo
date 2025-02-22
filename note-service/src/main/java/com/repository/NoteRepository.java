package com.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.dto.PatientRiskDto;
import com.model.Note;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NoteRepository extends ReactiveMongoRepository<Note, String> {
    
	 public Flux<Note> findByPatientId(String id);
	 
		@Aggregation(pipeline = {
				"{$addFields: {termsTrigger: {$regexFindAll: {input: '$content',regex:?1,options: 'i'}}}}",
	            "{$set: { termsTrigger: { $map: { input: '$termsTrigger.match', in: { $toLower: '$$this'}}}}}",
	            "{$unwind: {path: '$termsTrigger', includeArrayIndex: 'string',preserveNullAndEmptyArrays: true}}",
	            "{$group: { _id: '$patient._id', termsTrigger: { $addToSet: '$termsTrigger'}}}",
	            "{ $project: { count: { $size: '$termsTrigger'}}}}"})
			Mono<PatientRiskDto> computeTriggers(Long patientId, String regex);
		
		public Mono<Void> deleteByPatientId(String patientId);
		

	
}