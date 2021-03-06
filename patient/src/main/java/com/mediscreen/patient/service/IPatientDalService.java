package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;

import java.util.Collection;

public interface IPatientDalService {

    Patient create(Patient patient);
    Patient update(Patient patient);
    Collection<Patient> findAll();
    Patient getPatientByLastName(String lastName);
    Patient getPatient(int id);
    void deletePatient(int id);
    void deleteAllPatients();
}
