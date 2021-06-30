package com.mediscreen.note.service;

import com.mediscreen.note.model.Note;
import com.mediscreen.note.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <b>DAL Service for Note</b>
 * <p>All CRUD operations for Note working with MongoDb (noSQL)</p>
 */
@Slf4j
@Service
public class NoteDalService implements INoteDalService {

    @Autowired
    private NoteRepository noteRepository;

    /**
     * <b>Get Patient Historical Notes</b>
     * @param patientId mandatory id
     * @return A list of Note
     */
    @Override
    public List<Note> getHistoricalNotes(Integer patientId) {
        log.debug("Call to noteDalService.getHistoricalNotes");
        return noteRepository.findByPatientIdOrderByRecordDateAsc(patientId);
    }

    /**
     * <b>Create Note</b>
     * @param note mandatory
     * @return note when persistence successful
     */
    @Override
    public Note create(Note note) {
        log.debug("Call to noteDalService.create");
        return noteRepository.save(note);
    }


}
