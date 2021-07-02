package com.mediscreen.note.service;

import com.mediscreen.note.model.Note;

import java.util.List;

public interface INoteDalService {

    List<Note> getHistoricalNotes(Integer patientId);
    Note create(Note note);
    void delete(String id);

}
