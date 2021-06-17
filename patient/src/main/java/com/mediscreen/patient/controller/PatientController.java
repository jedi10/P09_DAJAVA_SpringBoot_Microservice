package com.mediscreen.patient.controller;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.exception.PatientUniquenessConstraintException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientDalServiceBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientDalServiceBean patientDalService;

    public PatientController(PatientDalServiceBean patientDalService) {
        this.patientDalService = patientDalService;
    }

    @PostMapping("/add")
    public Patient addPatient(@RequestParam String family, @RequestParam String given,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
                              @RequestParam String sex, @RequestParam String address, @RequestParam String phone,
                              HttpServletRequest request, HttpServletResponse response) throws PatientUniquenessConstraintException {
        log.info("Patient Microservice: addPatient EndPoint: URI= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        Patient patient = new Patient(sex, given, family, dob, address, phone);
        return patientDalService.create(patient);
    }

    @GetMapping("/")
    public Collection<Patient> getAllPatients(HttpServletRequest request, HttpServletResponse response) {
        log.info("Patient Microservice: getAllPatients EndPoint: URI= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.findAll();
    }

    @GetMapping()
    public Patient getPatient(@RequestParam Integer id,
                              HttpServletRequest request, HttpServletResponse response) throws PatientNotFoundException {
        log.info("Patient Microservice: getPatient with id EndPoint: URI= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.getPatient(id);
    }

    @GetMapping("/familyName")
    public Patient getPatientByFamilyName(@RequestParam String familyName,
                                          HttpServletRequest request, HttpServletResponse response) throws PatientNotFoundException {
        log.info("Patient Microservice: getPatient with lastName EndPoint: URI= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.getPatientByLastName(familyName);
    }




}
