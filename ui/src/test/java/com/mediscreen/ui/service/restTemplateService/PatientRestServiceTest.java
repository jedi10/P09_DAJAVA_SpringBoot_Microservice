package com.mediscreen.ui.service.restTemplateService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.ui.model.Patient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PatientRestService.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientRestServiceTest {

    @Autowired
    private PatientRestService patientRestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        this.mockServer.reset();
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getPatientList() throws JsonProcessingException {
        //Given
        List<Patient> patientListGiven = new ArrayList<>();
        patientListGiven.add(new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test"));

        patientListGiven.get(0).setId(0);
        String json = this.objectMapper
                .writeValueAsString(patientListGiven);
        this.mockServer
                .expect(requestTo("http://localhost:8081/patient/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        List<Patient> patientListResult = patientRestService.getPatientList();

        //THEN
        this.mockServer.verify();
        assertNotNull(patientListResult);
        assertTrue(patientListResult.size() == 1);
        assertEquals(patientListGiven.get(0), patientListResult.get(0));
    }
}