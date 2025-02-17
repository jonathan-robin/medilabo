package controller;

import com.service.controller.PatientController;
import com.service.model.Patient;
import com.service.service.PatientService;

import jakarta.validation.constraints.AssertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;

    public Patient patient;
    private List<Patient> patientList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
        patient.setGender("M");
        patient.setAddress("123 Test St");
        patient.setPhoneNumber("123-456-7890");
    }

    @Test
    void testGetPatientById() throws Exception {
        when(patientService.findPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    @Test
    void testGetPatientById_NotFound() throws Exception {
        when(patientService.findPatientById(1L)).thenReturn(null);

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePatient() throws Exception {
        when(patientService.savePatient(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Test\", \"lastName\": \"Patient\", \"birthDate\": \"1990-01-01\", \"gender\": \"M\", \"address\": \"123 Test St\", \"phoneNumber\": \"123-456-7890\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    @Test
    void testUpdatePatient() throws Exception {
        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(patient);

        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Test\", \"lastName\": \"Patient\", \"birthDate\": \"1990-01-01\", \"gender\": \"M\", \"address\": \"123 Test St\", \"phoneNumber\": \"123-456-7890\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }
}