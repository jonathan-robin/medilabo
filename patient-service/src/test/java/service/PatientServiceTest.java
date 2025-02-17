package service;

import com.service.model.Patient;
import com.service.repository.PatientRepository;
import com.service.service.PatientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testFindPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.findPatientById(1L);
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
    }

    @Test
    void testFindPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        Patient result = patientService.findPatientById(1L);
        assertNull(result);
    }

    @Test
    void testSavePatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.savePatient(patient);
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
    }

    @Test
    void testUpdatePatient_Success() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.updatePatient(1L, patient);
        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
    }

    @Test
    void testUpdatePatient_NotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        Patient result = patientService.updatePatient(1L, patient);
        assertNull(result);
    }
}