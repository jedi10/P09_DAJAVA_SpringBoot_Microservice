package com.mediscreen.risk.controller;

import com.mediscreen.risk.model.Risk;
import com.mediscreen.risk.services.RiskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"Microservice Risk Controller"})
@Tag(name = "Microservice Risk Controller", description = "Risk Microservice gives patient Risk")
@RestController
@RequestMapping(value="/")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @ApiOperation(value = "Get Patient Risk from PatientId", response= String.class, notes= "/access/id?patId=123")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get Patient Risk"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("access/id")
    public String getAssessmentStringForPatient(@RequestParam Integer patId)
    {
        return riskService.getRisk(patId).toString();
    }

    @ApiOperation(value = "Get Patient Risk from familyName", response= String.class, notes= "/access/familyName?familyName=dupont")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get Patient Risk"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("access/familyName")
    public String getAssessmentStringForPatient(@RequestParam String familyName)
    {
        return riskService.getRisk(familyName).toString();
    }

    @ApiOperation(value = "Get Patient Risk from PatientID", response= Risk.class, notes= "/risk/id?patId=123")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get Patient Risk"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("risk/id")
    public Risk getRiskForPatient(@RequestParam Integer patId)
    {
        return riskService.getRisk(patId);
    }

    @ApiOperation(value = "Get Patient Risk from familyName", response= Risk.class, notes= "/risk/familyName?familyName=dupont")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get Patient Risk"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("risk/familyName")
    public Risk getRiskForPatient(@RequestParam String familyName)
    {
        return riskService.getRisk(familyName);
    }
}