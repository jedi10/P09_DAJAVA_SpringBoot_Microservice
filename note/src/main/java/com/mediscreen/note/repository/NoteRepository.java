package com.mediscreen.note.repository;

import com.mediscreen.note.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * <b>MongoDb repository interface for Note Model</b>
 * @see com.mediscreen.note.model.Note
 */
public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findByPatientIdOrderByRecordDateAsc(Integer patientId);
}
