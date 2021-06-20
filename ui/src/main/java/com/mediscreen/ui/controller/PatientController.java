package com.mediscreen.ui.controller;

import com.mediscreen.ui.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/patient/")
public class PatientController {

    private static List<Patient> patientList = new ArrayList<>();

    static {
        patientList.add(new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test"));
        patientList.add(new Patient("M","firstName","lastName", LocalDate.of(2010,1,25),"address","phone"));
    }

    @GetMapping(value = "list")
    public String list(Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("UI: Get Patient List on URI: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        model.addAttribute("patients", PatientController.patientList);
        return "patient/list";
    }

    @GetMapping(value = "add")
    public String addPatientForm(Patient patient, Model model,
                             HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("patient", new Patient());
        log.info("UI: load Patient Form on URI: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        return "patient/add";
    }

    @PostMapping(value = "validate") // consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE+";charset=UTF-8"})//"application/x-www-form-urlencoded")
    public String validate(@Valid Patient patient,
                           BindingResult result,
                           Model model,
                           HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.warn("Patient Creation Error on URI: '{}': Error Field(s): '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    result.getFieldErrors().stream()
                            .map(e-> e.getField().toUpperCase())
                            .distinct()
                            .collect(Collectors.joining(", ")),
                    response.getStatus());
            return "patient/add";
        }
        PatientController.patientList.add(patient);
        Patient patientCreated = patient;
        patientCreated.setId(1);
        log.info("UI: Patient Creation on URI: '{}' : Patient Created '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                patientCreated.getId() + " " + patientCreated.getLastName(),
                response.getStatus());

        model.addAttribute("patients", PatientController.patientList);
        return "patient/list";
    }






}
