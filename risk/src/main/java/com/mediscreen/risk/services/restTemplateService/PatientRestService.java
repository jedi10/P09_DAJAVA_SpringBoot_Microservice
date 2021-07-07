package com.mediscreen.risk.services.restTemplateService;

import com.mediscreen.risk.exception.NotFoundException;
import com.mediscreen.risk.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class PatientRestService {
    @Autowired
    RestTemplate restTemplate;

    public static final String className = PatientRestService.class.getSimpleName();
    public static final String patientDockerURI = "http://patient:8085";
    public static final String patientURL = "/patient";

    /**
     * <b>Get Patient by Id: RestTemplate Call Patient Microservice</b>
     * @param i id is mandatory
     * @return Patient if found
     */
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

    /**
     * <b>Get Patient by FamilyName: RestTemplate Call Patient Microservice</b>
     * @param s familyName is mandatory
     * @return Patient if found
     */
    public Patient getByFamilyName(String s) {
        String logMessage = String.format("UI: call to %s.getPatientByFamilyName() ",
                className);
        log.debug(logMessage);
        String httpUrl = String.format("%s%s%s",
                patientDockerURI,
                patientURL,
                "/familyName");
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(httpUrl).
                        queryParam("familyName", s);//?familyName=dupont
        try {
            ResponseEntity<Patient> responseEntity = restTemplate.getForEntity(
                    uriComponentsBuilder.toUriString(),
                    Patient.class);
            return responseEntity.getBody();
        } catch (RestClientException exception) {
            String errorMessage = String.format("Exception during %s.getPatientByFamilyName : %s",
                    className,
                    exception.getMessage());
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}
