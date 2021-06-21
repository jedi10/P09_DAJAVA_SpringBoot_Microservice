package com.mediscreen.ui.service.restTemplateService;

import com.mediscreen.ui.model.Patient;
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
public class PatientRestService {

    @Autowired
    RestTemplate restTemplate;

    public static final String className = PatientRestService.class.getSimpleName();
    public static final String patientURL = "/patient";

    public List<Patient> getPatientList() {
        String logMessage = String.format("UI: call to %s.getPatientList()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s%s",
                "http://localhost:",
                8081,
                patientURL,
                "/");

        try {
            ResponseEntity<Patient[]> responseEntity = restTemplate.getForEntity(
                    httpUrl,
                    Patient[].class);
            return List.of(Objects.requireNonNull(responseEntity.getBody()));
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.getPatientList : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            return null;
        }
    }

}
