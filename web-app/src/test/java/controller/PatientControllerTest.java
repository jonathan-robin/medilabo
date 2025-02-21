package controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.controller.PatientController;
import com.dto.PatientDto;
import com.service.CookieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import jakarta.servlet.http.HttpServletRequest;


class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WebClient webClient;

    @Mock
    private CookieService cookieService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
    }

    @Test
    void testGetAllPatients_withValidJwt() throws Exception {
        // Arrange
        String jwt = "valid-jwt-token";
        PatientDto[] patients = new PatientDto[] { new PatientDto() };
        when(cookieService.getCookie(any(HttpServletRequest.class))).thenReturn(jwt);
        when(webClient.get().uri("/patients").header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(PatientDto[].class))
                .thenReturn(Mono.just(patients));

        // Act & Assert
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("patients", patients));
    }

    @Test
    void testGetPatientDetails_withValidJwt() throws Exception {
        // Arrange
        String jwt = "valid-jwt-token";
        String patientId = "1";
        PatientDto patient = new PatientDto();
        when(cookieService.getCookie(any(HttpServletRequest.class))).thenReturn(jwt);
        when(webClient.get().uri("/patients/" + patientId).header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(PatientDto.class))
                .thenReturn(Mono.just(patient));

        // Act & Assert
        mockMvc.perform(get("/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attribute("patient", patient));
    }

    @Test
    void testEditPatient_withValidJwt() throws Exception {
        // Arrange
        String jwt = "valid-jwt-token";
        Long patientId = 1L;
        PatientDto patientDto = new PatientDto();
        when(cookieService.getCookie(any(HttpServletRequest.class))).thenReturn(jwt);
        when(webClient.put().uri("/patients/" + patientId).bodyValue(patientDto).header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(PatientDto.class))
                .thenReturn(Mono.just(patientDto));

        // Act & Assert
        mockMvc.perform(post("/patients/edit/{id}", patientId).param("name", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attribute("patient", patientDto));
    }

    @Test
    void testSavePatient_withValidJwt() throws Exception {
        // Arrange
        String jwt = "valid-jwt-token";
        PatientDto patientDto = new PatientDto();
        when(cookieService.getCookie(any(HttpServletRequest.class))).thenReturn(jwt);
        when(webClient.post().uri("/patients").bodyValue(patientDto).header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(PatientDto[].class))
                .thenReturn(Mono.just(new PatientDto[] { patientDto }));

        // Act & Assert
        mockMvc.perform(post("/patients/save").flashAttr("patient", patientDto))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("patients", new PatientDto[] { patientDto }));
    }

    @Test
    void testDeletePatient_withValidJwt() throws Exception {
        // Arrange
        String jwt = "valid-jwt-token";
        Long patientId = 1L;
        when(cookieService.getCookie(any(HttpServletRequest.class))).thenReturn(jwt);
        when(webClient.delete().uri("/patients/" + patientId).header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(PatientDto[].class))
                .thenReturn(Mono.just(new PatientDto[] {}));

        // Act & Assert
        mockMvc.perform(get("/patients/delete/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("patients", new PatientDto[] {}));
    }
}
