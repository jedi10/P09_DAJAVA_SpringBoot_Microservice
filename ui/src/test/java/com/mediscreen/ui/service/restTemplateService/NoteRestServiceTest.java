package com.mediscreen.ui.service.restTemplateService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.ui.exception.NotFoundException;
import com.mediscreen.ui.exception.NoteCrudException;
import com.mediscreen.ui.model.Note;
import com.mediscreen.ui.model.Patient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(NoteRestService.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteRestServiceTest {

    @Autowired
    private NoteRestService noteRestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockServer;

    public static final String noteDockerURI = "http://note:8071";
    public static final String noteURL = "/note";

    @BeforeEach
    void setUp() {
        this.mockServer.reset();
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getNoteList() throws JsonProcessingException {
        //Given
        List<Note> noteListGiven = new ArrayList<>();
        int patientId = 1;
        noteListGiven.add(new Note(patientId,"premiere visite au centre medical; injection vaccin", LocalDate.now()));
        noteListGiven.get(0).setId("0");
        String json = this.objectMapper
                .writeValueAsString(noteListGiven);

        String httpUrl = String.format("%s%s",
                noteDockerURI,
                noteURL);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("patientId", patientId);//?patientId=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        List<Note> noteListResult = noteRestService.getList(patientId);

        //THEN
        this.mockServer.verify();
        assertNotNull(noteListResult);
        assertEquals(1, noteListResult.size());
        assertEquals(noteListGiven.get(0), noteListResult.get(0));
    }

    @Order(2)
    @Test
    void addNote() throws JsonProcessingException {
        //Given
        int patientId = 1;
        Note noteGiven = new Note(patientId,"premiere_visite_au_centre_medical;_injection_vaccin", LocalDate.now());
        noteGiven.setId("0");
        String json = this.objectMapper
                .writeValueAsString(noteGiven);
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "/add");

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl)
                        .queryParam("patientId", noteGiven.getPatientId())
                        .queryParam("note", noteGiven.getNote());
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Note noteResult = noteRestService.add(noteGiven);

        //THEN
        this.mockServer.verify();
        assertNotNull(noteResult);
        assertEquals(noteGiven, noteResult);
        //String noteDecode = URLDecoder.decode(noteResult.getNote(), StandardCharsets.UTF_8);
    }

    @Order(3)
    @Test
    void getNoteById() throws JsonProcessingException {
        //Given
        String idOnTest = "0";
        int patientId = 1;
        Note noteGiven = new Note(patientId,"premiere_visite_au_centre_medical;_injection_vaccin",
                LocalDate.now().minus(3, ChronoUnit.DAYS));
        noteGiven.setId(idOnTest);
        String json = this.objectMapper
                .writeValueAsString(noteGiven);
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "/get");

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Note noteResult = noteRestService.getById(idOnTest);

        //THEN
        this.mockServer.verify();
        assertNotNull(noteResult);
        assertEquals(noteGiven, noteResult);
    }

    @Order(4)
    @Test
    void getNoteById_error() {
        //Given
        String idOnTest = "0";
        int patientId = 1;
        Note noteGiven = new Note(patientId,"premiere_visite_au_centre_medical;_injection_vaccin",
                LocalDate.now().minus(3, ChronoUnit.DAYS));
        noteGiven.setId(idOnTest);

        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "/get");

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("Not Found")));
        //WHEN
        Exception exception = assertThrows(NotFoundException.class,
                ()-> noteRestService.getById(idOnTest)
        );

        //THEN
        this.mockServer.verify();
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Exception during"));
        assertTrue(exception.getMessage().contains(".getNoteById"));
        assertTrue(exception.getMessage().contains("Not Found"));
    }

    @Order(5)
    @Test
    void deleteNoteById_ok() {
        //Given
        String idOnTest = "0";
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "/delete");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());
        //WHEN
        noteRestService.deleteById(idOnTest);

        //THEN
        this.mockServer.verify();
    }

    @Order(6)
    @Test
    void deleteNoteById_error() {
        //Given
        String idOnTest = "0";
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "/delete");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", idOnTest);//?id=0
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withException(new IOException()));
        //WHEN
        assertThrows(NoteCrudException.class,
                ()-> noteRestService.deleteById(idOnTest)
        );

        //THEN
        this.mockServer.verify();
    }

    @Order(7)
    @Test
    void updateNote() throws JsonProcessingException {
        //Given
        String idOnTest = "0";
        int patientId = 1;
        Note noteGiven = new Note(patientId,"premiere_visite_au_centre_medical;_injection_vaccin",
                LocalDate.now().minus(3, ChronoUnit.DAYS));
        noteGiven.setId(idOnTest);

        String json = this.objectMapper
                .writeValueAsString(noteGiven);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("http://note:8071/note/update")
                        .queryParam("id", noteGiven.getId())
                        .queryParam("patientId", noteGiven.getPatientId())
                        .queryParam("note", noteGiven.getNote());
        this.mockServer
                .expect(requestTo(uriComponentsBuilder.toUriString()))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        //WHEN
        Note noteResult = noteRestService.update(noteGiven);

        //THEN
        this.mockServer.verify();
        assertNotNull(noteResult);
        assertEquals(noteGiven, noteResult);
    }
}