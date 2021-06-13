package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;

import java.util.Collection;

public interface IPatientDalService {

    Patient create(Patient patient);
    Collection<Patient> findAll();
    Patient getPatient(int id);
}
