package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class PatientDalServiceBean implements IPatientDalService {

    private PatientRepository patientRepository;

    public PatientDalServiceBean(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * <b>Create Patient</b>
     * @param patient mandatory
     * @return patient when persistence successful
     */
    @Override
    public Patient create(@NonNull Patient patient) {
        log.debug("Call to patientDalService.create");
        return patientRepository.save(patient);
    }

    /**
     * <b>Give all Patients</b>
     * @return a collection of patients
     */
    @Override
    public Collection<Patient> findAll() {
        log.debug("Call to patientDalService.findAll");
        return patientRepository.findAll();
    }
    /**
     * <b>Get Patient with Id</b>
     * @param id mandatory
     * @return Patient
     */
    @Override
    public Patient getPatient(int id) {
        log.debug("Call to patientDalService.getPatient");
        return patientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }
}
