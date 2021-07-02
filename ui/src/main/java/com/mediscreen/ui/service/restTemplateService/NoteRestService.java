package com.mediscreen.ui.service.restTemplateService;

import com.mediscreen.ui.exception.NoteCrudException;
import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.model.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NoteRestService {

    @Autowired
    RestTemplate restTemplate;

    public static final String className = PatientRestService.class.getSimpleName();
    public static final String noteDockerURI = "http://note:8071";
    public static final String noteURL = "/note/";

    public List<Note> getList(int patientId) {
        String logMessage = String.format("UI: call to %s.getNoteList()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s",
                noteDockerURI,
                noteURL);

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("patientId", patientId);//?patientId=0

        try {
            ResponseEntity<Note[]> responseEntity = restTemplate.getForEntity(
                    uriComponentsBuilder.toUriString(),
                    Note[].class);
            return List.of(Objects.requireNonNull(responseEntity.getBody()));
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.getNoteList : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new NoteCrudException(errorMessage);
        }
    }

    public Note add(Note note){
        String logMessage = String.format("UI: call to %s.addNote()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "add");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl)
                        .queryParam("patientId", note.getPatientId())
                        .queryParam("note", note.getNote());

        try {
            ResponseEntity<Note> responseEntity = restTemplate.postForEntity(
                    uriComponentsBuilder.toUriString(),
                    null,
                    Note.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.addNote: %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new NoteCrudException(errorMessage);
        }
    }

    public void deleteById(String i) {
        String logMessage = String.format("UI: call to %s.deleteNoteById() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                noteDockerURI,
                noteURL,
                "delete");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", i);//?id=0

        try {
            restTemplate.delete(
                    uriComponentsBuilder.toUriString());
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.deleteNoteById : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new NoteCrudException(errorMessage);
        }
    }
}
