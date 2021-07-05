package com.mediscreen.note.service;

import com.mediscreen.note.exception.NoteNotFoundException;
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

    /**
     * <b>Delete Note</b>
     * @param id mandatory
     */
    @Override
    public void delete(String id) {
        log.debug("Call to noteDalService.delete");
        noteRepository.findById(id)
                .orElseThrow(()-> new NoteNotFoundException("Note not found with id: "+ id));
        noteRepository.deleteById(id);
    }

    /**
     * <b>Get a Note By Id</b>
     * @param id mandatory and String type (MongoDB)
     * @return a Note object
     */
    @Override
    public Note getById(String id) {
        log.debug("Call to noteDalService.getNote");
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id:" + id));
    }

    /**
     * <b>Update a note</b>
     * @param note mandatory
     * @return a Note Object
     */
    @Override
    public Note update(Note note) {
        log.debug("Call to noteDalService.update");
        Note previousNote = noteRepository.findById(note.getId())
                .orElseThrow(() -> {
                    log.debug("updateNote: Note not found");
                    return new NoteNotFoundException("Note not found with id:" + note.getId());
                });
        if(previousNote != null){
            //keep historical safe
            note.setRecordDate(previousNote.getRecordDate());
            return noteRepository.save(note);
        }
        return null;
    }
}
