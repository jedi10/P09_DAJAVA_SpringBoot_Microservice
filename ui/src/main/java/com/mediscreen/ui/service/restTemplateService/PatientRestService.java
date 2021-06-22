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

    public Patient getPatientById(int i) {
        String logMessage = String.format("UI: call to %s.getPatientById() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                "http://localhost:",
                8081,
                patientURL);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", i);//?id=0
        try {
            ResponseEntity<Patient> responseEntity = restTemplate.getForEntity(
                    uriComponentsBuilder.toUriString(),
                    Patient.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.getPatientById : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            return null;
        }
    }

    public void deletePatientById(int i) {
        String logMessage = String.format("UI: call to %s.deletePatientById() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                "http://localhost:",
                8081,
                patientURL);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("id", i);//?id=0

        try {
            restTemplate.delete(
                    uriComponentsBuilder.toUriString());
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.deletePatientById : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
        }
    }

    public Patient addPatient(Patient patient){
        String logMessage = String.format("UI: call to %s.addPatient()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s%s",
                "http://localhost:",
                8081,
                patientURL,
                "/add");
        try {
            ResponseEntity<Patient> responseEntity = restTemplate.postForEntity(
                    httpUrl,
                    patient,
                    Patient.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.addPatient: %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            return null;
        }
    }

}
