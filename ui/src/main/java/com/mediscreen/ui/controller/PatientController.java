package com.mediscreen.ui.controller;

import com.mediscreen.ui.model.Patient;
import com.mediscreen.ui.tool.Snippets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = {"UI Patient Controller"})
@Tag(name = "UI Patient Controller", description = "Patient UI (HTML Page)")
@Slf4j
@Controller
@RequestMapping("/patient/")
public class PatientController {

    private static List<Patient> patientList = new ArrayList<>();

    static {
        patientList.add(new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test"));
        patientList.get(0).setId(0);
        patientList.add(new Patient("M","firstName","lastName", LocalDate.of(2010,1,25),"address","phone"));
        patientList.get(1).setId(1);
    }

    @ApiOperation(value = "Show List of Patient", response= String.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show a list of Patient"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "list")
    public String list(Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("UI: Get Patient List on URI: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        model.addAttribute("patients", PatientController.patientList);
        return "patient/list";
    }

    @ApiOperation(value = "Show Form to create a Patient", response = String.class, notes = "Show an empty form to create a Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show form to create a new Patient"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to see the patient creation form"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "add")
    public String addPatientForm(Model model,
                             HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("patient", new Patient());
        log.info("UI: load Patient Form on URI: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        return "patient/add";
    }

    @ApiOperation(value = "Validate a Patient", response = String.class, notes = "User send form to validate and create a Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully validate a new Patient"),
            @ApiResponse(responseCode = "401", description = "Already registered: you are not authorized to create twice the same Patient"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
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

        Patient patientCreated = patient;
        patientCreated.setId(Snippets.getRandomNumberInRange(2,1000));
        PatientController.patientList.add(patientCreated);
        log.info("UI: Patient Creation on URI: '{}' : Patient Created '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                patientCreated.getId() + " " + patientCreated.getLastName(),
                response.getStatus());

        model.addAttribute("patients", PatientController.patientList);
        return "patient/list";
    }

    @ApiOperation(value = "Delete specific Patient with the supplied Patient id", notes= "/patient/delete/23")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deletes the specific Patient"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to delete is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id, Model model,
                                HttpServletRequest request, HttpServletResponse response) {
        Optional<Patient> found = Optional.empty();
        for (Patient e : PatientController.patientList) {
            if (e.getId().equals(id)) {
                found = Optional.of(e);
                break;
            }
        }
        if (found.isPresent()){
            Patient patientToRemove =  found.get();
            PatientController.patientList.remove(patientToRemove);
            log.info("Delete Patient on URI: '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    response.getStatus());
        }


        /**
        Optional<Patient> patientOptional = PatientRepository.findById(id);
        if(patientOptional.isPresent()){
            patientRepository.deleteById(id);
            log.info("Delete Patient on URI: '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    response.getStatus());
        } else {
            log.warn("No Patient was deleted on URI: '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    response.getStatus());
        }*/

        model.addAttribute("patients", PatientController.patientList);
        return "patient/list";
    }






}