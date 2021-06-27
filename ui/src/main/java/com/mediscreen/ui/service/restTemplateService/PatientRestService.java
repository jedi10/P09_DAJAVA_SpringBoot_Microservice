package com.mediscreen.ui.service.restTemplateService;

import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.exception.NotFoundException;
import com.mediscreen.ui.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
    public static final String patientDockerURI = "http://patient:8081";
    public static final String patientURL = "/patient";

    public List<Patient> getList() {
        String logMessage = String.format("UI: call to %s.getPatientList()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                patientDockerURI,
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
            throw new PatientCrudException(errorMessage);
        }
    }

    public Patient getById(int i) {
        String logMessage = String.format("UI: call to %s.getPatientById() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s",
                patientDockerURI,
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
            throw new NotFoundException(errorMessage);
        }
    }

    public void deleteById(int i) {
        String logMessage = String.format("UI: call to %s.deletePatientById() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s",
                patientDockerURI,
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
            throw new PatientCrudException(errorMessage);
        }
    }

    public Patient add(Patient patient){
        String logMessage = String.format("UI: call to %s.addPatient()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                patientDockerURI,
                patientURL,
                "/add");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl)
                        .queryParam("family", patient.getLastName())
                        .queryParam("given", patient.getFirstName())
                        .queryParam("dob", patient.getBirthDate())
                        .queryParam("sex", patient.getSex())
                        .queryParam("address", patient.getAddress())
                        .queryParam("phone", patient.getPhone());
        try {
            ResponseEntity<Patient> responseEntity = restTemplate.postForEntity(
                    uriComponentsBuilder.toUriString(),
                    null,
                    Patient.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.addPatient: %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new PatientCrudException(errorMessage);
        }
    }

    public Patient update(Patient patient){
        String logMessage = String.format("UI: call to %s.updatePatient()",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                patientDockerURI,
                patientURL,
                "/update");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl)
                        .queryParam("id", patient.getId())
                        .queryParam("family", patient.getLastName())
                        .queryParam("given", patient.getFirstName())
                        .queryParam("dob", patient.getBirthDate())
                        .queryParam("sex", patient.getSex())
                        .queryParam("address", patient.getAddress())
                        .queryParam("phone", patient.getPhone());
        try {
            ResponseEntity<Patient> responseEntity = restTemplate.exchange(
                    uriComponentsBuilder.toUriString(),
                    HttpMethod.PUT,
                    null,
                    Patient.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.updatePatient: %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new PatientCrudException(errorMessage);
        }
    }

}
