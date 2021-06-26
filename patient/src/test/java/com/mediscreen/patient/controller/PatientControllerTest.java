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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)//@AutoConfigureMockMvc
//https://spring.io/guides/gs/testing-web/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientDalServiceBean patientDalServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patientGiven;

    @BeforeAll
    void setUpAll(){
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
    void addPatient() throws Exception {
        //GIVEN
        when(patientDalServiceMock.create(patientGiven)).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/patient/add").
                contentType(MediaType.APPLICATION_JSON).param("family", patientGiven.getLastName())
                .param("sex",patientGiven.getSex())
                .param("given",patientGiven.getFirstName())
                .param("dob",patientGiven.getBirthDate().toString())
                .param("address",patientGiven.getAddress())
                .param("phone",patientGiven.getPhone());

        MvcResult mvcResult =
                mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        verify(patientDalServiceMock, Mockito.times(1)).create(patientGiven);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(patientGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(2)
    @Test
    void addPatient_UniquenessConstraintError() throws Exception {
        //GIVEN
        when(patientDalServiceMock.create(patientGiven)).thenThrow(new PatientUniquenessConstraintException("Patient is already present"));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/patient/add").
                contentType(MediaType.APPLICATION_JSON).param("family", patientGiven.getLastName())
                .param("sex",patientGiven.getSex())
                .param("given",patientGiven.getFirstName())
                .param("dob",patientGiven.getBirthDate().toString())
                .param("address",patientGiven.getAddress())
                .param("phone",patientGiven.getPhone());


        mockMvc.perform(builder)//.andDo(print());
                        .andExpect(status().isUnauthorized());//HttpStatus.BAD_REQUEST 401

        verify(patientDalServiceMock, Mockito.times(1)).create(patientGiven);
    }

    @Order(3)
    @Test
    void getAllPatients() throws Exception {
        //GIVEN
        List<Patient> patientList = new ArrayList<>();
        patientList.add(new Patient("M","firstname","lastname", LocalDate.of(2020,1,15),"address","phone"));
        patientList.add(patientGiven);

        when(patientDalServiceMock.findAll()).thenReturn(patientList);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/patient/").
                contentType(MediaType.APPLICATION_JSON);
        //WHEN
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();
        //THEN
        verify(patientDalServiceMock, Mockito.times(1)).findAll();
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(patientList);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(4)
    @Test
    void getPatient() throws Exception {
        //GIVEN
        patientGiven.setId(5);
        when(patientDalServiceMock.getPatient(patientGiven.getId())).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/patient").
                contentType(MediaType.APPLICATION_JSON)
                .param("id", patientGiven.getId().toString());
        //WHEN
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andReturn();

        //THEN
        verify(patientDalServiceMock, Mockito.times(1)).getPatient(patientGiven.getId());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(patientGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(5)
    @Test
    void getPatient_NotFoundError() throws Exception {
        //GIVEN
        int idGiven = 5;
        patientGiven.setId(idGiven);
        when(patientDalServiceMock.getPatient(patientGiven.getId())).thenThrow(new PatientNotFoundException("Patient not found with id: "+ idGiven));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/patient").
                contentType(MediaType.APPLICATION_JSON)
                .param("id", patientGiven.getId().toString());
        //WHEN
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //THEN
        verify(patientDalServiceMock, Mockito.times(1)).getPatient(patientGiven.getId());
    }

    @Order(6)
    @Test
    void getPatientByFamilyName() throws Exception {
        //GIVEN
        when(patientDalServiceMock.getPatientByLastName(patientGiven.getLastName())).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/patient/familyName").
                contentType(MediaType.APPLICATION_JSON)
                .param("familyName", patientGiven.getLastName());
        //WHEN
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andReturn();

        //THEN
        verify(patientDalServiceMock, Mockito.times(1)).getPatientByLastName(patientGiven.getLastName());
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(patientGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(7)
    @Test
    void updatePatient() throws Exception {
        //GIVEN
        patientGiven.setId(1);
        when(patientDalServiceMock.update(patientGiven)).thenReturn(patientGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/patient/update").
                contentType(MediaType.APPLICATION_JSON)
                .param("id", patientGiven.getId().toString())
                .param("family", patientGiven.getLastName())
                .param("sex",patientGiven.getSex())
                .param("given",patientGiven.getFirstName())
                .param("dob",patientGiven.getBirthDate().toString())
                .param("address",patientGiven.getAddress())
                .param("phone",patientGiven.getPhone());

        MvcResult mvcResult =
                mockMvc.perform(builder)//.andDo(print());
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        verify(patientDalServiceMock, Mockito.times(1)).update(patientGiven);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(patientGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }
}



//@WebMvcTest is intended to test unitarily the controller from the server side.
//@SpringBootTest, on the other hand, should be used for integration tests, when you want to interact with the application from the client side.
//https://stackoverflow.com/questions/39865596/difference-between-using-mockmvc-with-springboottest-and-using-webmvctest