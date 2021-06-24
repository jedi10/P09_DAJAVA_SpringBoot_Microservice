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
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
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
                .expect(requestTo("http://patient:8081/patient/"))
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

    @Order(2)
    @Test
    void getPatientById() throws JsonProcessingException {
        //Given
        int idOnTest = 0;
        Patient patientGiven = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
        patientGiven.setId(idOnTest);
        String json = this.objectMapper
                .writeValueAsString(patientGiven);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://patient:8081/patient").
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Patient patientResult = patientRestService.getPatientById(idOnTest);

        //THEN
        this.mockServer.verify();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }

    @Order(3)
    @Test
    void deletePatientById() throws JsonProcessingException {
        //Given
        int idOnTest = 0;
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://patient:8081/patient").
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());
        //WHEN
        patientRestService.deletePatientById(idOnTest);

        //THEN
        this.mockServer.verify();
    }

    @Order(4)
    @Test
    void addPatient() throws JsonProcessingException {
        //Given
        Patient patientGiven = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
        patientGiven.setId(0);
        String json = this.objectMapper
                .writeValueAsString(patientGiven);
        this.mockServer
                .expect(requestTo("http://patient:8081/patient/add"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Patient patientResult = patientRestService.addPatient(patientGiven);

        //THEN
        this.mockServer.verify();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }
}