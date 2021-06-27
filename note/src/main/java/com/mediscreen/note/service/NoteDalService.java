package com.mediscreen.note.service;

import com.mediscreen.note.model.Note;
import com.mediscreen.note.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <b>DAL Service</b>
 * <p>All CRUD operations for Note</p>
 */
@Service
public class NoteDalService implements INoteDalService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public List<Note> getHistoricalNotes(Integer patientId) {
        return null;
    }
}
