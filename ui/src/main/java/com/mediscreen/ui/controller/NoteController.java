package com.mediscreen.ui.controller;

import com.mediscreen.ui.exception.NoteCrudException;
import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.model.Note;
import com.mediscreen.ui.model.Patient;
import com.mediscreen.ui.service.restTemplateService.NoteRestService;
import com.mediscreen.ui.service.restTemplateService.PatientRestService;
import com.mediscreen.ui.tool.Snippets;
import io.swagger.annotations.Api;
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

    private Boolean localMode = true;

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

        } /**else {
            try {
                Patient patient = patientRestService.getById(patientId);
                model.addAttribute("patient", patient);
                model.addAttribute("note", new Note());
                return "note/add";
            } catch (PatientCrudException e) {
                model.addAttribute("errorAddingNote", e.getMessage());
                return patientController.list(model, request, response);
            }
        }**/
        return "note/add";
    }

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
            //model.addAttribute("notes", NoteController.noteList);
        } /**else {
            try {
                model.addAttribute("patient", patientRestService.getById(note.getPatientId()));
                noteCreated = noteRestService.add(note);
                model.addAttribute("notes", noteRestService.getList(patientId));

            } catch (PatientCrudException | NoteCrudException e) {
                model.addAttribute("errorAddingNote", e.getMessage());
                return "note/add";
            }
        }**/
        log.info("UI: Note Creation on URL: '{}' : Note Created for PatientId '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                noteCreated.getPatientId() + ": " + noteCreated.getNote(),
                response.getStatus());

        return "redirect:/note/"+ noteCreated.getPatientId() +"/list";
    }
}
