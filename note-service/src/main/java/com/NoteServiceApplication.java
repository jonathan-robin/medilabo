package com;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.service.NoteService;
//import com.utils.ImportJson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class NoteServiceApplication implements ApplicationRunner {
	
	private final NoteService noteService;

	private String jsonFile = "notes.json";

	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			Resource jsonData = new ClassPathResource(jsonFile);
			log.info("Json data: {}", jsonData);
			InputStream jsonStream = jsonData.getInputStream();
			log.info("jsonStream: {}", jsonStream);
			noteService.importAll(jsonStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
