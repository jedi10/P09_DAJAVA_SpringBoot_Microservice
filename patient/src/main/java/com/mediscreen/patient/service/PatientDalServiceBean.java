package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientDalServiceBean implements IPatientDalService {

    private PatientRepository patientRepository;

    public PatientDalServiceBean(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient create(@NonNull Patient patient) {
        log.debug("Call to patientDalService.createPatient");
        return patientRepository.save(patient);
    }
}
