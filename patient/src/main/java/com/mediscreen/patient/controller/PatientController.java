package com.mediscreen.patient.controller;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.exception.PatientUniquenessConstraintException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientDalServiceBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientDalServiceBean patientDalService;

    @PostMapping("/add")
    public Patient addPatient(@RequestParam String family, @RequestParam String given,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
                              @RequestParam String sex, @RequestParam String address, @RequestParam String phone) throws PatientUniquenessConstraintException {
        Patient patient = new Patient(sex, given, family, dob, address, phone);
        return patientDalService.create(patient);
    }

    public PatientController(PatientDalServiceBean patientDalService) {
        this.patientDalService = patientDalService;
    }

    @GetMapping("/")
    public Collection<Patient> getAllPatients() {
        return patientDalService.findAll();
    }

    @GetMapping()
    public Patient getPatient(@RequestParam Integer id) throws PatientNotFoundException {
        return patientDalService.getPatient(id);
    }

    @GetMapping("/familyName")
    public Patient getPatientByFamilyName(@RequestParam String familyName) throws PatientNotFoundException {
        return patientDalService.getPatientByLastName(familyName);
    }




}
