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
                .expect(requestTo("http://patient:8085/patient/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        List<Patient> patientListResult = patientRestService.getList();

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
                UriComponentsBuilder.fromHttpUrl("http://patient:8085/patient").
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Patient patientResult = patientRestService.getById(idOnTest);

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
                UriComponentsBuilder.fromHttpUrl("http://patient:8085/patient").
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());
        //WHEN
        patientRestService.deleteById(idOnTest);

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
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://patient:8085/patient/add")
                        .queryParam("family", patientGiven.getLastName())
                        .queryParam("given", patientGiven.getFirstName())
                        .queryParam("dob", patientGiven.getBirthDate())
                        .queryParam("sex", patientGiven.getSex())
                        .queryParam("address", patientGiven.getAddress())
                        .queryParam("phone", patientGiven.getPhone());
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Patient patientResult = patientRestService.add(patientGiven);

        //THEN
        this.mockServer.verify();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }

    @Order(5)
    @Test
    void updatePatient() throws JsonProcessingException {
        //Given
        Patient patientGiven = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
        patientGiven.setId(0);
        String json = this.objectMapper
                .writeValueAsString(patientGiven);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://patient:8085/patient/update")
                        .queryParam("id", patientGiven.getId())
                        .queryParam("family", patientGiven.getLastName())
                        .queryParam("given", patientGiven.getFirstName())
                        .queryParam("dob", patientGiven.getBirthDate())
                        .queryParam("sex", patientGiven.getSex())
                        .queryParam("address", patientGiven.getAddress())
                        .queryParam("phone", patientGiven.getPhone());
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Patient patientResult = patientRestService.update(patientGiven);

        //THEN
        this.mockServer.verify();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }
}