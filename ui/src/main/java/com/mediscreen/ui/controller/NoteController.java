package com.mediscreen.ui.controller;

import com.mediscreen.ui.exception.NotFoundException;
import com.mediscreen.ui.exception.NoteCrudException;
import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.model.Note;
import com.mediscreen.ui.model.Patient;
import com.mediscreen.ui.service.restTemplateService.NoteRestService;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = {"UI Note Controller"})
@Tag(name = "UI Note Controller", description = "Note UI (HTML Page) showing patient historical")
@Slf4j
@Controller
@RequestMapping("/note/")
public class NoteController {

    @Autowired
    private PatientController patientController;

    @Autowired
    private NoteRestService noteRestService;

    @Autowired
    private PatientRestService patientRestService;

    private static List<Note> noteList = new ArrayList<>();
    private static Patient patient;

    Boolean localMode = false;

    static {
        int patientId = 1;
        noteList.add(new Note(patientId,"premiere visite au centre medical; injection vaccin",
                LocalDate.now().minus(14, ChronoUnit.DAYS)));
        noteList.get(0).setId("0");
        noteList.add(new Note(patientId, "Seconde visite au centre medical: confirmation injection vaccin",
                LocalDate.now().minus(2, ChronoUnit.DAYS)));
        noteList.get(1).setId("1");
        patient = new Patient("M","Dmitri","Gloukhovski",
                LocalDate.of(1979,6,12),"Moscou","phone1_Test");
        patient.setId(patientId);
    }

    @ApiOperation(value = "Show List of Note for one Patient", response= String.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show a list of Note"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "{patientId}/list")
    public String list(@PathVariable("patientId") Integer patientId,
                       Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("UI: Get Note List on URL: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());

        if (localMode){
            model.addAttribute("patient", NoteController.patient);
            model.addAttribute("notes", NoteController.noteList);
        } else {
            try {
                Patient patient = patientRestService.getById(patientId);
                model.addAttribute("patient", patient);
                List<Note> notes = noteRestService.getList(patientId);
                model.addAttribute("notes", notes);
            } catch (NoteCrudException | PatientCrudException e) {
                model.addAttribute("errorListingNotes", e.getMessage());
            }
        }
        return "note/list";
    }

    @ApiOperation(value = "Show Form to create a Note", response = String.class, notes = "Show an empty form to create a Note for one Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show form to create a new Note"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to see the note creation form"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "{patientId}/add")
    public String addNoteForm(@PathVariable Integer patientId,
                              Model model,
                              HttpServletRequest request, HttpServletResponse response) {
        log.info("UI: load Note Form on URL: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        if (localMode){
            model.addAttribute("patient", NoteController.patient);
            model.addAttribute("note", new Note());

        } else {
            try {
                Patient patient = patientRestService.getById(patientId);
                model.addAttribute("patient", patient);
                model.addAttribute("note", new Note());
            } catch (PatientCrudException e) {
                model.addAttribute("errorAddingNote", e.getMessage());
                return patientController.list(model, request, response);
            }
        }
        return "note/add";
    }

    @ApiOperation(value = "Validate a Note", response = String.class, notes = "User send form to validate and create a Note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully validate a new Note"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to create Note"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @PostMapping(value = "{patientId}/validate") // consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE+";charset=UTF-8"})//"application/x-www-form-urlencoded")
    public String validate(@PathVariable Integer patientId,
                           @Valid Note note,
                           BindingResult result,
                           Model model,
                           HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.warn("UI: Note Creation Error on URL: '{}': Error Field(s): '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    result.getFieldErrors().stream()
                            .map(e-> e.getField().toUpperCase())
                            .distinct()
                            .collect(Collectors.joining(", ")),
                    response.getStatus());
            return "redirect:/note/"+ patientId +"/add";
        }
        Note noteCreated = null;
        if (localMode){
            noteCreated = note;
            noteCreated.setId(String.valueOf(Snippets.getRandomNumberInRange(2,1000)));
            noteCreated.setRecordDate(LocalDate.now());
            NoteController.noteList.add(noteCreated);
        } else {
            try {
                //Make sure we have a patient
                patientRestService.getById(note.getPatientId());
                noteCreated = noteRestService.add(note);

            } catch (PatientCrudException | NoteCrudException e) {
                model.addAttribute("errorAddingNote", e.getMessage());
                return "note/add";
            }
        }
        log.info("UI: Note Creation on URL: '{}' : Note Created for PatientId '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                noteCreated.getPatientId() + ": " + noteCreated.getNote(),
                response.getStatus());

        return "redirect:/note/"+ noteCreated.getPatientId() +"/list";
    }

    @ApiOperation(value = "Delete specific Note with the supplied Note id", notes= "/note/delete/1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deletes the specific Note"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to delete is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping(value = "{patientId}/delete/{id}")
    public String deleteNote(@PathVariable Integer patientId, @PathVariable("id") String id,
                             Model model,
                             HttpServletRequest request, HttpServletResponse response) {
        Optional<Note> found = Optional.empty();
        if (localMode){
            for (Note e : NoteController.noteList) {
                if (e.getId().equals(id)) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()){
                Note noteToRemove =  found.get();
                NoteController.noteList.remove(noteToRemove);
                log.info("UI: Delete Note on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
            }
        } else {

            Note noteResult = null;
            try {
                log.info("UI: Delete Note on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
                noteResult = noteRestService.getById(id);
                noteRestService.deleteById(id);

            } catch (NotFoundException e) {
                log.warn("UI: No Note was deleted on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
                model.addAttribute("errorListingNotes", e.getMessage());
            }
        }
        return "redirect:/note/"+ patientId +"/list";
    }

    @ApiOperation(value = "Show Form to update a Note", response = String.class, notes = "Show a filled form to update a Note for one Patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully show form to update a new Note"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to see the patient creation form"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @GetMapping("{patientId}/update/{id}")
    public String updateNoteForm(@PathVariable Integer patientId, @PathVariable("id") String id,
                                 Model model,
                                 HttpServletRequest request, HttpServletResponse response) {
        if (localMode){
            Optional<Note> found = Optional.empty();
            for (Note e : NoteController.noteList) {
                if (e.getId().equals(id)) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()){
                Note noteToUpdate =  found.get();
                model.addAttribute("note", noteToUpdate);
                model.addAttribute("patient", NoteController.patient);
                log.info("UI: Show Update Note Form on URL: '{}' : RESPONSE STATUS: '{}'",
                        request.getRequestURI(),
                        response.getStatus());
            }
        } else {
            try {
                Patient patient = patientRestService.getById(patientId);
                model.addAttribute("patient", patient);
                Note note = noteRestService.getById(id);
                model.addAttribute("note", note);
            }
            catch (NotFoundException notFoundException)
            {
                model.addAttribute("errorUpdatingNote", notFoundException.getMessage());
                return "redirect:/note/"+ patientId +"/list";
            }
        }
        return "note/update";
    }

    @ApiOperation(value = "Validate a Note", response = String.class, notes = "User send form to validate and update a Note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully validate a Note update"),
            @ApiResponse(responseCode = "401", description = "you are not authorized to update Note"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")
    }
    )
    @PostMapping(value = "{patientId}/update") // consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE+";charset=UTF-8"})//"application/x-www-form-urlencoded")
    public String updateNote(@PathVariable Integer patientId,
                           @Valid Note note,
                           BindingResult result,
                           Model model,
                           HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.warn("UI: Note Update Error on URL: '{}': Error Field(s): '{}' : RESPONSE STATUS: '{}'",
                    request.getRequestURI(),
                    result.getFieldErrors().stream()
                            .map(e-> e.getField().toUpperCase())
                            .distinct()
                            .collect(Collectors.joining(", ")),
                    response.getStatus());
            return "redirect:/note/"+ patientId +"/update/"+ note.getId();
        }
        Note noteToUpdate = null;
        if (localMode){
            Optional<Note> found = Optional.empty();
            for (Note e : NoteController.noteList) {
                if (e.getId().equals(note.getId())) {
                    found = Optional.of(e);
                    break;
                }
            }
            if (found.isPresent()) {
                noteToUpdate = found.get();
                int index = NoteController.noteList.indexOf(noteToUpdate);
                NoteController.noteList.get(index).setNote(note.getNote());
            }
        } else {
            try {
                //Make sure we have a patient
                patientRestService.getById(note.getPatientId());
                //Make sure we have a Note
                noteRestService.getById(note.getId());
                noteToUpdate = noteRestService.update(note);

            } catch (PatientCrudException | NoteCrudException e) {
                model.addAttribute("errorUpdatingNote", e.getMessage());
                return "note/update";
            }
        }
        log.info("UI: Note Update on URL: '{}' : Note Updated for PatientId '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                noteToUpdate.getPatientId() + ": " + noteToUpdate.getNote(),
                response.getStatus());

        return "redirect:/note/"+ noteToUpdate.getPatientId() +"/list";
    }
}
