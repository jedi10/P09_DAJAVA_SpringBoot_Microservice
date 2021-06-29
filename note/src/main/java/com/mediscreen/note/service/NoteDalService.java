package com.mediscreen.note.service;

import com.mediscreen.note.model.Note;
import com.mediscreen.note.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <b>DAL Service</b>
 * <p>All CRUD operations for Note</p>
 */
@Slf4j
@Service
public class NoteDalService implements INoteDalService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public List<Note> getHistoricalNotes(Integer patientId) {
        log.debug("Call to noteDalService.getHistoricalNotes");
        return noteRepository.findByPatientIdOrderByRecordDateAsc(patientId);
    }
}
