package com.mediscreen.patient.service;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.exception.PatientUniquenessConstraintException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * <b>All CRUD operations for Patient</b>
 * <p>use of a repository working with a Mysql database</p>
 */
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
        Optional<Patient> previousPatient = patientRepository
                .findByFirstNameAndLastNameAndBirthDateAllIgnoreCase(patient.getFirstName(), patient.getLastName(), patient.getBirthDate());
        if (previousPatient.isPresent()){
            log.debug("createPatient: uniqueness constraint violation: patient already present");
            throw new PatientUniquenessConstraintException("Patient is already present");
        } else {
            return patientRepository.save(patient);
        }
    }

    @Override
    public Patient update(Patient patient) {
        log.debug("Call to patientDalService.update");
        patientRepository.findById(patient.getId())
                .orElseThrow(() -> {
                            log.debug("updatePatient: patient not present");
                            return new PatientNotFoundException("Patient not found with id:" + patient.getId()+ ": update impossible");
                        });
        Optional<Patient> previousPatient = patientRepository
                .findByFirstNameAndLastNameAndBirthDateAllIgnoreCase(patient.getFirstName(), patient.getLastName(), patient.getBirthDate());

        if (previousPatient.isPresent() && !previousPatient.get().getId().equals(patient.getId())) {
            log.debug("updatePatient : Patient with same firstname, lastname and birthdate already exist");
            throw new PatientUniquenessConstraintException("Patient with same firstname, lastname and birthdate already exist: update impossible");
        } else {
            return patientRepository.save(patient);
        }
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
     * <b>Get Patient with LastName</b>
     * @param lastName mandatory
     * @return Patient
     */
    @Override
    public Patient getPatientByLastName(String lastName) {
        log.debug("Call to patientDalService.getPatientByLastName");
        return patientRepository.findByLastNameIgnoreCase(lastName)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with lastName:" + lastName));
    }

    /**
     * <b>Get Patient with Id</b>
     * @param id mandatory
     * @return Patient
     */
    @Override
    public Patient getPatient(int id) {
        log.debug("Call to patientDalService.getPatient");
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id:" + id));
    }

    @Override
    public void deletePatient(int id) {
        patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id:" + id));
        patientRepository.deleteById(id);
    }

    @Override
    public void deleteAllPatients() {
        patientRepository.deleteAll();
    }
}
