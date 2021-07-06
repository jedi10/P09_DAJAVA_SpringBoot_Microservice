package com.mediscreen.risk.services;

import com.mediscreen.risk.model.Note;
import com.mediscreen.risk.model.Patient;
import com.mediscreen.risk.model.Risk;
import com.mediscreen.risk.model.RiskEnum;
import com.mediscreen.risk.services.restTemplateService.NoteRestService;
import com.mediscreen.risk.services.restTemplateService.PatientRestService;
import com.mediscreen.risk.utils.ListUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RiskServiceTest {

    @MockBean
    private PatientRestService patientRestService;

    @MockBean
    private NoteRestService noteRestService;

    @Autowired
    private RiskService riskService;

    List<Note> buildNoteWithRisk(int riskNumber)
    {
        List<Note> noteBuildList = new ArrayList<>();
        StringJoiner noteDetails = null;
        for (int i = 0; i < riskNumber; i++) {
            noteDetails = new StringJoiner(" ", "Mes notes du jour: ", ";");
            noteDetails.add(ListUtils.getRiskFactors().get(i));
            noteBuildList.add(new Note(1, noteDetails.toString(), LocalDate.now()));
        }
        return noteBuildList;
    }

    @Order(1)
    @Test
    void countIterationRisk(){
        //Given
        int riskNumber = 3;
        List<Note> noteGiven = buildNoteWithRisk(riskNumber);

        //When
        long result = RiskService.countRiskIteration(noteGiven);

        //Then
        assertEquals(riskNumber, result);
    }

    @Order(2)
    @Test
    void getRiskByPatientId(){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1996,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getById(patient.getId())).thenReturn(patient);

        int riskNumber = 3;
        List<Note> noteGiven = buildNoteWithRisk(riskNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getId());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskEnum.BORDERLINE, risk.getRiskEnum());
    }

    @Order(3)
    @Test
    void getRiskByPatientLastName(){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1996,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        int riskNumber = 3;
        List<Note> noteGiven = buildNoteWithRisk(riskNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskEnum.BORDERLINE, risk.getRiskEnum());
    }



}