package com.mediscreen.note.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.note.model.Note;
import com.mediscreen.note.service.NoteDalService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NoteController.class)//@AutoConfigureMockMvc
//https://spring.io/guides/gs/testing-web/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteDalService noteDalServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private Note noteGiven;
    private int patientId = 1;

    @BeforeAll
    void setUpAll(){
        noteGiven = new Note(patientId,"premiere visite au centre medical; injection vaccin", LocalDate.now());
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getHistoricalNotes() throws Exception {
        //GIVEN
        List<Note> historicalGiven = new ArrayList<>();
        historicalGiven.add(noteGiven);
        historicalGiven.add(new Note(patientId, "Seconde visite au centre medical: confirmation injection vaccin", LocalDate.now()));

        when(noteDalServiceMock.getHistoricalNotes(patientId)).thenReturn(historicalGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/note")
                .contentType(MediaType.APPLICATION_JSON)
                .param("patientId", String.valueOf(patientId));
        //WHEN
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();
        //THEN
        verify(noteDalServiceMock, Mockito.times(1)).getHistoricalNotes(patientId);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(historicalGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(2)
    @Test
    void addNote() throws Exception {
        //GIVEN
        when(noteDalServiceMock.create(noteGiven)).thenReturn(noteGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/note/add").
                contentType(MediaType.APPLICATION_JSON)
                        .param("patientId", noteGiven.getPatientId().toString())
                        .param("note",noteGiven.getNote());

        MvcResult mvcResult =
                mockMvc.perform(builder)//.andDo(print());
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn();

        verify(noteDalServiceMock, Mockito.times(1)).create(noteGiven);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String jsonResult = mvcResult.getResponse().getContentAsString();
        String expectedJson = objectMapper.writeValueAsString(noteGiven);
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(3)
    @Test
    void deleteNote() throws Exception {
        //GIVEN
        noteGiven.setId("1");
        Mockito.doNothing().when(noteDalServiceMock).delete(noteGiven.getId());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/note")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", noteGiven.getId());

        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk());

        verify(noteDalServiceMock, Mockito.times(1)).delete(noteGiven.getId());
    }

    @Order(4)
    @Test
    void getNoteById() throws Exception {
        //GIVEN
        noteGiven.setId("1");
        when(noteDalServiceMock.getById(noteGiven.getId())).thenReturn(noteGiven);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/note/get")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", noteGiven.getId());

        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk());

        verify(noteDalServiceMock, Mockito.times(1)).getById(noteGiven.getId());
    }
}