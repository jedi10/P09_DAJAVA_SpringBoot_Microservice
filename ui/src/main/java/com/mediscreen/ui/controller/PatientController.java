package com.mediscreen.ui.controller;

import com.mediscreen.ui.exception.NotFoundException;
import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.model.Patient;
import com.mediscreen.ui.service.restTemplateService.PatientRestService;
import com.mediscreen.ui.tool.Snippets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PatientRestService patientRestService;

    private Boolean localMode = false;

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
        log.info("UI: Get Patient List on URL: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        if (localMode){
            model.addAttribute("patients", PatientController.patientList);
        } else {
            try {
                List<Patient> patients = patientRestService.getList();
                model.addAttribute("patients", patients);
            } catch (PatientCrudException e) {
                model.addAttribute("errorListingPatients", e.getMessage());
            }
        }
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
        log.info("UI: load Patient Form on URL: '{}' : RESPONSE STATUS: '{}'",
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
            log.warn("UI: Patient Creation Error on URL: '{}': Error Field(s): '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    result.getFieldErrors().stream()
                            .map(e-> e.getField().toUpperCase())
                            .distinct()
                            .collect(Collectors.joining(", ")),
                    response.getStatus());
            return "patient/add";
        }
        Patient patientCreated = null;
        if (localMode){
            patientCreated = patient;
            patientCreated.setId(Snippets.getRandomNumberInRange(2,1000));
            PatientController.patientList.add(patientCreated);
            model.addAttribute("patients", PatientController.patientList);
        } else {
            try {
                patientCreated = patientRestService.add(patient);
                model.addAttribute("patients", patientRestService.getList());
            } catch (PatientCrudException e) {
                model.addAttribute("errorAddingPatient", e.getMessage());
                return "patient/add";
            }
        }
        log.info("UI: Patient Creation on URL: '{}' : Patient Created '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                patientCreated.getId() + " " + patientCreated.getLastName(),
                response.getStatus());

        return "patient/list";
    }

    @ApiOperation(value = "Show Form to update a Patient", response = String.class, notes = "Show a filled form to update a Patient; /patient/update/23")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show form to update a Patient"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to see the patient update form"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("update/{id}")
    public String updatePatientForm(@PathVariable("id") Integer id, Model model,
                                    HttpServletRequest request, HttpServletResponse response) {
        if (localMode){
            Optional<Patient> found = Optional.empty();
            for (Patient e : PatientController.patientList) {
                if (e.getId().equals(id)) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()){
                Patient patientToUpdate =  found.get();
                model.addAttribute("patient", patientToUpdate);
                log.info("UI: Show Update Patient Form on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
            }
        } else {
            try {
                Patient patient = patientRestService.getById(id);
                model.addAttribute("patient", patient);
            }
            catch (NotFoundException patientNotFoundException)
            {
                model.addAttribute("errorUpdatingPatient", patientNotFoundException.getMessage());
                model.addAttribute("patient", new Patient());
            }
        }
        return "patient/update";
    }

    @ApiOperation(value = "Validate a Patient for Update", response = String.class, notes = "User send form to validate and update a Patient; /patient/update/23")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully validate data for updating Patient"),
            @ApiResponse(responseCode = "401", description = "Already registered: you are not authorized to create twice the same Patient"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @PostMapping("update/{id}")
    public String updatePatient(@PathVariable("id") Integer id, @Valid Patient patient, BindingResult result, Model model,
                                HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.warn("UI: Patient Update Error on URL: '{}': Error Field(s): '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    result.getFieldErrors().stream()
                            .map(e-> e.getField().toUpperCase())
                            .distinct()
                            .collect(Collectors.joining(", ")),
                    response.getStatus());
            return "patient/update";
        }

        if(localMode){
            Optional<Patient> found = Optional.empty();
            for (Patient e : PatientController.patientList) {
                if (e.getId().equals(id)) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()) {
                Patient patientToUpdate = found.get();
                int index = PatientController.patientList.indexOf(patientToUpdate);
                PatientController.patientList.set(index, patient);
                log.info("Patient Update on URL: '{}' : Patient Updated '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        patient.getId() + " " + patient.getLastName(),
                        response.getStatus());
                model.addAttribute("patients", PatientController.patientList);
            }
        } else {
            try {
                Patient patientUpdated = patientRestService.update(patient);
                log.info("Patient Update on URL: '{}' : Patient Updated '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        patientUpdated.getId() + " " + patientUpdated.getLastName(),
                        response.getStatus());
                model.addAttribute("patients", patientRestService.getList());
                return "redirect:/patient/list";
            } catch (PatientCrudException exception) {
                model.addAttribute("errorUpdatingPatient", exception.getMessage());
                return "patient/update";
            }
        }
        return "redirect:/patient/list";
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
        if (localMode){
            for (Patient e : PatientController.patientList) {
                if (e.getId().equals(id)) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()){
                Patient patientToRemove =  found.get();
                PatientController.patientList.remove(patientToRemove);
                log.info("UI: Delete Patient on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
            }
            model.addAttribute("patients", PatientController.patientList);
        } else {

            Patient patientResult = null;
            try {
                patientResult = patientRestService.getById(id);
                patientRestService.deleteById(id);
                log.info("UI: Delete Patient on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
            } catch (NotFoundException e) {
                log.warn("UI: No Patient was deleted on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
                model.addAttribute("errorListingPatients", e.getMessage());
            }
        }
        return "patient/list";
    }
}
