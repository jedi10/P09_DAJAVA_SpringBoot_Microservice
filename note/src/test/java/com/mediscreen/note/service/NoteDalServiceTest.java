package com.mediscreen.note.service;

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

}