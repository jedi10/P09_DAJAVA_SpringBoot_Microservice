package com.mediscreen.risk.services.restTemplateService;

import com.mediscreen.risk.exception.NoteCrudException;
import com.mediscreen.risk.model.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NoteRestService {

    @Autowired
    RestTemplate restTemplate;

    public static final String className = NoteRestService.class.getSimpleName();
    public static final String noteDockerURI = "http://note:8071";
    public static final String noteURL = "/note";

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
}
