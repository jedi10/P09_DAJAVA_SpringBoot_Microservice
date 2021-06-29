package com.mediscreen.note.controller;

import com.mediscreen.note.model.Note;
import com.mediscreen.note.service.NoteDalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@Api(tags = {"Microservice Note Controller"})
@Tag(name = "Microservice Note Controller", description = "Note Microservice deals with Patient Historical Notes")
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteDalService noteDalService;

    @ApiOperation(value = "Get List of Note (Patient Historical)", response= Collection.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get a list of Note"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("")
    public List<Note> getHistoricalNotes(@RequestParam Integer patientId) {
        return noteDalService.getHistoricalNotes(patientId);
    }
}
