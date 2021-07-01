package com.mediscreen.ui.service.restTemplateService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.ui.model.Note;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
    public static final String noteURL = "/note/";

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
                "add");

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
}