package com.mediscreen.ui.controller;

import com.mediscreen.ui.exception.PatientCrudException;
import com.mediscreen.ui.model.Note;
import com.mediscreen.ui.model.Patient;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"UI Note Controller"})
@Tag(name = "UI Note Controller", description = "Note UI (HTML Page) showing patient historical")
@Slf4j
@Controller
@RequestMapping("/note/")
public class NoteController {

    private static List<Note> noteList = new ArrayList<>();

    private Boolean localMode = true;

    static {
        int patientId = 1;
        noteList.add(new Note(patientId,"premiere visite au centre medical; injection vaccin", LocalDate.now()));
        noteList.get(0).setId("0");
        noteList.add(new Note(patientId, "Seconde visite au centre medical: confirmation injection vaccin", LocalDate.now()));
        noteList.get(1).setId("1");
    }

    @GetMapping(value = "{patientId}/list")
    public String list(@PathVariable("patientId") Integer patientId,
                       Model model, HttpServletRequest request, HttpServletResponse response){
        log.info("UI: Get Note List on URL: '{}' : RESPONSE STATUS: '{}'",
                request.getRequestURI(),
                response.getStatus());
        if (localMode){
            model.addAttribute("notes", NoteController.noteList);
        } /**else {
            try {
                List<Note> notes = noteRestService.getList();
                model.addAttribute("notes", notes);
            } catch (NoteCrudException e) {
                model.addAttribute("errorListingNotes", e.getMessage());
            }
        }**/
        return "note/list";
    }
}
