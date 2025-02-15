package com.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    @GetMapping("/patients")
    public String getAllPatients() {
        return "Liste des patients";
    }
}
