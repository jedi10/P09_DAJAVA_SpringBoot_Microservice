package com.mediscreen.ui.controller;

import com.mediscreen.ui.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/patient/")
public class PatientController {

    @GetMapping(value = "list")
    public String list(Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("UI: Get Patient List on URI: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        List<Patient> patientList = new ArrayList<>();
        patientList.add(new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test"));
        patientList.add(new Patient("M","firstName","lastName", LocalDate.of(2010,1,25),"address","phone"));

        model.addAttribute("patients", patientList);
        return "patient/list";
    }




}
