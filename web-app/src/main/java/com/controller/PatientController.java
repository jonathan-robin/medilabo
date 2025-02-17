package com.controller;


import com.dto.PatientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final RestTemplate restTemplate;
    private final String apiGatewayUrl;

    public PatientController(RestTemplate restTemplate, @Value("${api.gateway.url}") String apiGatewayUrl) {
        this.restTemplate = restTemplate;
        this.apiGatewayUrl = apiGatewayUrl;
    }

    @GetMapping("")
    public String getAllPatients(Model model) {
        String url = apiGatewayUrl + "/patients";
        ResponseEntity<PatientDto[]> response = restTemplate.getForEntity(url, PatientDto[].class);
        List<PatientDto> patients = Arrays.asList(response.getBody());
        model.addAttribute("patients", patients);
        return "patient-list";
    }

    @GetMapping("/{id}")
    public String getPatientDetails(@PathVariable Long id, Model model) {
        String url = apiGatewayUrl + "/patients/" + id;
        ResponseEntity<PatientDto> response = restTemplate.getForEntity(url, PatientDto.class);
        model.addAttribute("patient", response.getBody());
        return "patient-details";
    }

    @GetMapping("/new")
    public String showAddPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "patient-form";
    }

    @PostMapping("")
    public String savePatient(@ModelAttribute PatientDto patient) {
        String url = apiGatewayUrl + "/patients";
        restTemplate.postForEntity(url, patient, Void.class);
        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String showEditPatientForm(@PathVariable Long id, Model model) {
        String url = apiGatewayUrl + "/patients/" + id;
        ResponseEntity<PatientDto> response = restTemplate.getForEntity(url, PatientDto.class);
        model.addAttribute("patient", response.getBody());
        return "patient-form";
    }

    @PostMapping("/update/{id}")
    public String updatePatient(@PathVariable Long id, @ModelAttribute PatientDto patient) {
        String url = apiGatewayUrl + "/patients/" + id;
        restTemplate.put(url, patient);
        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        String url = apiGatewayUrl + "/patients/" + id;
        restTemplate.delete(url);
        return "redirect:/patients";
    }
}
