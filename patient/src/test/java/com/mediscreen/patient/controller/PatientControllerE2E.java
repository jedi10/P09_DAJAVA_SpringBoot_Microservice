package com.mediscreen.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.exception.PatientUniquenessConstraintException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientDalServiceBean;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//https://spring.io/guides/gs/testing-web/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientControllerE2E {

    @Autowired
    private TestRestTemplate template;
    private HttpHeaders headers;

    private final String rootUrl= "http://localhost:";

    @LocalServerPort
    private int port;

    private Patient patientGiven;

    @BeforeAll
    void setUpAll(){
        //***********GIVEN*************
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        patientGiven = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void addPatient() {
        //GIVEN
        String urlTemplate = this.rootUrl + port + "/patient/add";
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(urlTemplate)
                        .queryParam("family", patientGiven.getLastName())
                        .queryParam("given", patientGiven.getFirstName())
                        .queryParam("dob", patientGiven.getBirthDate())
                        .queryParam("sex", patientGiven.getSex())
                        .queryParam("address", patientGiven.getAddress())
                        .queryParam("phone", patientGiven.getPhone());

        HttpEntity<Patient> requestEntity = new HttpEntity<>(null, headers);

        //***********WHEN*************
        ResponseEntity<Patient> result = template.postForEntity(
                uriComponentsBuilder.toUriString(),
                requestEntity,
                Patient.class);
        //**************THEN***************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200
        assertNotNull(result.getBody());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        Patient patientResult = result.getBody();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);

        //for Next Test
        patientGiven.setId(patientResult.getId());
    }

    @Order(2)
    @Test
    void getAllPatients() throws Exception {
        //GIVEN
        String urlTemplate = this.rootUrl + port + "/patient/";
        Patient[] patientList = new Patient[1];
        patientList[0] = patientGiven;
        //WHEN
        ResponseEntity<Patient[]> result = template.getForEntity(
                urlTemplate,
                Patient[].class);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200
        assertNotNull(result.getBody());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        Patient[] patientResult = result.getBody();
        assertNotNull(patientResult);
        assertEquals(patientList[0], patientResult[0]);
    }

    @Order(3)
    @Test
    void getPatient() throws Exception {
        //GIVEN
        String urlTemplate = this.rootUrl + port + "/patient";
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(urlTemplate)
                        .queryParam("id", patientGiven.getId().toString());
        //WHEN
        ResponseEntity<Patient> result = template.getForEntity(
                uriComponentsBuilder.toUriString(),
                Patient.class);
        //THEN
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200
        assertNotNull(result.getBody());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        Patient patientResult = result.getBody();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }

    @Order(4)
    @Test
    void getPatientByFamilyName() {
        //GIVEN
        String urlTemplate = this.rootUrl + port + "/patient/familyName";
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(urlTemplate)
                        .queryParam("familyName", patientGiven.getLastName());
        //WHEN
        ResponseEntity<Patient> result = template.getForEntity(
                uriComponentsBuilder.toUriString(),
                Patient.class);

        //THEN
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200
        assertNotNull(result.getBody());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        Patient patientResult = result.getBody();
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
    }

    @Order(5)
    @Test
    void deletePatientById() {
        //GIVEN
        String urlTemplate = this.rootUrl + port + "/patient";
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(urlTemplate)
                        .queryParam("id", patientGiven.getId().toString());
        //WHEN
        template.delete(
                uriComponentsBuilder.toUriString());
    }
}



//@WebMvcTest is intended to test unitarily the controller from the server side.
//@SpringBootTest, on the other hand, should be used for integration tests, when you want to interact with the application from the client side.
//https://stackoverflow.com/questions/39865596/difference-between-using-mockmvc-with-springboottest-and-using-webmvctest