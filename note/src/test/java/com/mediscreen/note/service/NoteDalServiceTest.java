package com.mediscreen.note.service;

import com.mediscreen.note.exception.NoteNotFoundException;
import com.mediscreen.note.model.Note;
import com.mediscreen.note.repository.NoteRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NoteDalServiceTest {

    @Mock
    private NoteRepository noteRepositoryMock;

    @InjectMocks
    private NoteDalService noteDalService;

    private Note noteGiven;
    private int patientId = 1;

    @BeforeEach
    void setUp() {
        noteGiven = new Note(patientId,"première visite au centre médical; injection vaccin", LocalDate.now());
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getHistoricalNotes() {
        //GIVEN
        List<Note> patientHistoricalGiven = new ArrayList<>();
        patientHistoricalGiven.add(noteGiven);
        patientHistoricalGiven.add(new Note(patientId, "2ième visite au centre médical: 2ième injection vaccin", LocalDate.now()));
        when(noteRepositoryMock.findByPatientIdOrderByRecordDateAsc(patientId)).thenReturn(patientHistoricalGiven);
        //WHEN
        List<Note> patientHistoricalResult =  noteDalService.getHistoricalNotes(patientId);

        //THEN
        assertNotNull(patientHistoricalResult);
        assertEquals(patientHistoricalGiven.size(), patientHistoricalResult.size());
    }

    @Order(2)
    @Test
    void create() {
        //GIVEN
        when(noteRepositoryMock.save(noteGiven)).thenReturn(noteGiven);
        verify(noteRepositoryMock, Mockito.never()).save(noteGiven);

        //WHEN
        Note noteResult = noteDalService.create(noteGiven);

        //THEN
        assertNotNull(noteResult);
        assertEquals(noteGiven, noteResult);
        verify(noteRepositoryMock, Mockito.times(1)).save(noteGiven);
    }

    @Order(3)
    @Test
    void delete_ok() {
        //GIVEN
        noteGiven.setId("1");
        when(noteRepositoryMock.findById(noteGiven.getId())).thenReturn(java.util.Optional.ofNullable(noteGiven));
        Mockito.doNothing().when(noteRepositoryMock).deleteById(noteGiven.getId());

        //WHEN
        noteDalService.delete(noteGiven.getId());

        //THEN
        verify(noteRepositoryMock, Mockito.times(1)).findById(noteGiven.getId());
        verify(noteRepositoryMock, Mockito.times(1)).deleteById(noteGiven.getId());
    }

    @DisplayName("delete failed: note Not Found")
    @Order(4)
    @Test
    void delete_noteNotFound() {
        //GIVEN
        noteGiven.setId("1");
        when(noteRepositoryMock.findById(noteGiven.getId())).thenThrow(
                new NoteNotFoundException("Note not found with id"));
        Mockito.doNothing().when(noteRepositoryMock).deleteById(noteGiven.getId());

        //WHEN
        Exception exception = assertThrows(NoteNotFoundException.class,
                ()-> noteDalService.delete(noteGiven.getId())
        );

        //THEN
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Note not found with id"));
        verify(noteRepositoryMock, Mockito.never()).deleteById(noteGiven.getId());
    }

    @Order(5)
    @Test
    void getById_ok() {
        //GIVEN
        noteGiven.setId("1");
        when(noteRepositoryMock.findById(noteGiven.getId())).thenReturn(java.util.Optional.ofNullable(noteGiven));

        //WHEN
        noteDalService.getById(noteGiven.getId());

        //THEN
        verify(noteRepositoryMock, Mockito.times(1)).findById(noteGiven.getId());
    }

    @DisplayName("getById failed: note Not Found")
    @Order(6)
    @Test
    void getById_noteNotFound() {
        //GIVEN
        noteGiven.setId("1");
        when(noteRepositoryMock.findById(noteGiven.getId())).thenThrow(
                new NoteNotFoundException("Note not found with id"));

        //WHEN
        Exception exception = assertThrows(NoteNotFoundException.class,
                ()-> noteDalService.delete(noteGiven.getId())
        );

        //THEN
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Note not found with id"));
        verify(noteRepositoryMock, Mockito.times(1)).findById(noteGiven.getId());
    }

}

//https://stackoverflow.com/questions/2276271/how-to-mock-void-methods-with-mockito