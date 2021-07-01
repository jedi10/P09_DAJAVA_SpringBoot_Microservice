package com.mediscreen.note.controller;

import com.mediscreen.note.model.Note;
import com.mediscreen.note.service.NoteDalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
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
    public List<Note> getHistoricalNotes(@RequestParam Integer patientId,
                                         HttpServletRequest request, HttpServletResponse response) {
        log.info("Note Microservice: getAllNotes for PatientId: {} EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                patientId,
                request.getRequestURI(),
                response.getStatus());
        return noteDalService.getHistoricalNotes(patientId);
    }

    @ApiOperation(value = "Add a Note", response = Note.class, notes = "/note/add?patientId=1&note=first visite in center for vaccination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a new Note"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create Note"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @PostMapping("/add")
    public Note addNote(@RequestParam Integer patientId, @RequestParam String note,
                              HttpServletRequest request, HttpServletResponse response) {
        log.info("Note Microservice: addNote EndPoint: URL= '{}' : RESPONSE STATUS= '{}'",
                request.getRequestURI(),
                response.getStatus());
        Note noteToPersist = new Note(
                patientId,
                URLDecoder.decode(note, StandardCharsets.UTF_8),
                LocalDate.now());

        return noteDalService.create(noteToPersist);
    }


}