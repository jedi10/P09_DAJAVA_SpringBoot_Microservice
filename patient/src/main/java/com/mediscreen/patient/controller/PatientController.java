package com.mediscreen.patient.controller;

import com.mediscreen.patient.exception.PatientNotFoundException;
import com.mediscreen.patient.exception.PatientUniquenessConstraintException;
import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.service.PatientDalServiceBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collection;

@Api(tags = {"Microservice Patient Controller"})
@Tag(name = "Microservice Patient Controller", description = "Patient Microservice Resources")
@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientDalServiceBean patientDalService;

    public PatientController(PatientDalServiceBean patientDalService) {
        this.patientDalService = patientDalService;
    }

    @ApiOperation(value = "Add a Patient", response = Patient.class, notes = "/patient/add?family=Gloukhovski&given=Dmitri&dob=1979-06-12&sex=M&address=Moscou&phone=phone1_Test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a new Patient"),
            @ApiResponse(responseCode = "401", description = "Already registered: you are not authorized to create twice the same Patient"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @PostMapping("/add")
    public Patient addPatient(@RequestParam String family, @RequestParam String given,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
                              @RequestParam String sex, @RequestParam String address, @RequestParam String phone,
                              HttpServletRequest request, HttpServletResponse response) throws PatientUniquenessConstraintException {
        log.info("Patient Microservice: addPatient EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        Patient patient = new Patient(sex, given, family, dob, address, phone);
        return patientDalService.create(patient);
    }

    @ApiOperation(value = "Get List of Patient", response= Collection.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get a list of Patient"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("/")
    public Collection<Patient> getAllPatients(HttpServletRequest request, HttpServletResponse response) {
        log.info("Patient Microservice: getAllPatients EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.findAll();
    }

    @ApiOperation(value = "Get a Patient with id", response= Patient.class, notes= "/patient?id=23")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get the Patient with the supplied id"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping()
    public Patient getPatient(@RequestParam Integer id,
                              HttpServletRequest request, HttpServletResponse response) throws PatientNotFoundException {
        log.info("Patient Microservice: getPatient with id EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.getPatient(id);
    }

    @ApiOperation(value = "Get a Patient with lastName", response= Patient.class, notes= "/patient/familyName?familyName=gloukhovski")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get the Patient with the supplied familyName"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("/familyName")
    public Patient getPatientByFamilyName(@RequestParam String familyName,
                                          HttpServletRequest request, HttpServletResponse response) throws PatientNotFoundException {
        log.info("Patient Microservice: getPatient with lastName EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        return patientDalService.getPatientByLastName(familyName);
    }

    @ApiOperation(value = "Delete specific Patient with the supplied Patient id", notes= "/patient?id=23")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deletes the specific Patient"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to delete is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @DeleteMapping("")
    public void deletePatientById(@RequestParam Integer id,
                                          HttpServletRequest request, HttpServletResponse response) throws PatientNotFoundException {
        log.info("Patient Microservice: deletePatient with id EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        patientDalService.deletePatient(id);
    }


}


//https://medium.com/swlh/restful-api-documentation-made-easy-with-swagger-and-openapi-6df7f26dcad