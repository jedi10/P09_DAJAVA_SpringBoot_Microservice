package com.mediscreen.risk.services;

import com.mediscreen.risk.model.Note;
import com.mediscreen.risk.model.Patient;
import com.mediscreen.risk.model.Risk;
import com.mediscreen.risk.model.RiskLevelEnum;
import com.mediscreen.risk.services.restTemplateService.NoteRestService;
import com.mediscreen.risk.services.restTemplateService.PatientRestService;
import com.mediscreen.risk.utils.ListUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    List<Note> buildNoteWithDistinctRisk(int riskNumber)
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
        List<Note> noteGiven = buildNoteWithDistinctRisk(riskNumber);

        //When
        long result = RiskService.distinctRiskIteration(noteGiven);

        //Then
        assertEquals(riskNumber, result);
    }

    @Order(2)
    @Test
    void countIterationRiskWithDuplicate(){
        //Given
        int riskNumber = 3;
        List<Note> noteGiven = buildNoteWithDistinctRisk(riskNumber);
        String doublon = "Mes notes du jour:  "+ ListUtils.getRiskFactors().get(1)+";";
        noteGiven.add(new Note(1, doublon, LocalDate.now()));

        //When
        long result = RiskService.distinctRiskIteration(noteGiven);

        //Then
        assertEquals(riskNumber, result);
    }

    @Order(3)
    @Test
    void getRiskByPatientId(){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1996,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getById(patient.getId())).thenReturn(patient);

        int riskNumber = 1;
        List<Note> noteGiven = buildNoteWithDistinctRisk(riskNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getId());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.NONE, risk.getRiskLevelEnum());
    }

    @Order(4)
    @Test
    void getRiskByPatientLastName(){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1996,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        int riskNumber = 1;
        List<Note> noteGiven = buildNoteWithDistinctRisk(riskNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.NONE, risk.getRiskLevelEnum());
    }

    @Order(5)
    @ParameterizedTest
    @CsvSource({"0", "1"})
    void getRiskForLevel0(int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1956,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.NONE, risk.getRiskLevelEnum());
    }

    @Order(6)
    @ParameterizedTest
    @CsvSource({"1", "2"})
    void getRiskForLevel1_AgeLess30_2RiskFactor(int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(2015,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.NONE, risk.getRiskLevelEnum());
    }
    @Order(7)
    @ParameterizedTest
    @CsvSource({"2", "3"})
    void getRiskForLevel1_AgeUp30_2RiskFactor(int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,"M","Jery","TheCat", LocalDate.of(1956,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.BORDERLINE, risk.getRiskLevelEnum());
    }

    @Order(8)
    @ParameterizedTest
    @CsvSource({"M,3", "M,4"})
    void getRiskForLevel2_AgeLess30_M_3RiskFactor(String sexe, int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,sexe ,"Jery","TheCat", LocalDate.of(2015,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.DANGER, risk.getRiskLevelEnum());
    }

    @Order(9)
    @ParameterizedTest
    @CsvSource({"M,5", "M,6"})
    void getRiskForLevel2_AgeLess30_M_5RiskFactor(String sexe, int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,sexe ,"Jery","TheCat", LocalDate.of(2015,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.EARLY_ONSET, risk.getRiskLevelEnum());
    }

    @Order(10)
    @ParameterizedTest
    @CsvSource({"F,4", "F,5"})
    void getRiskForLevel2_AgeLess30_F_4RiskFactor(String sexe, int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,sexe ,"Jery","TheCat", LocalDate.of(2015,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.DANGER, risk.getRiskLevelEnum());
    }

    @Order(11)
    @ParameterizedTest
    @CsvSource({"F,7", "F,8"})
    void getRiskForLevel2_AgeLess30_F_7RiskFactor(String sexe, int riskFactorNumber){
        //Given
        Patient patient = new Patient(1,sexe,"Jery","TheCat", LocalDate.of(2015,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.EARLY_ONSET, risk.getRiskLevelEnum());
    }

    @Order(12)
    @ParameterizedTest
    @CsvSource({"6","7"})
    void getRiskForLevel2_AgeMore30_6RiskFactor(int riskFactorNumber){
        //Given
        Patient patient = new Patient(1, "M","Jery","TheCat", LocalDate.of(1970,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.DANGER, risk.getRiskLevelEnum());
    }

    @Order(13)
    @ParameterizedTest
    @CsvSource({"8","9"})
    void getRiskForLevel3_AgeMore30_8RiskFactor(int riskFactorNumber){
        //Given
        Patient patient = new Patient(1, "M","Jery","TheCat", LocalDate.of(1970,12,31),"1 Brookside St","100-222-3333");
        when(patientRestService.getByFamilyName(patient.getLastName())).thenReturn(patient);

        List<Note> noteGiven = buildNoteWithDistinctRisk(riskFactorNumber);
        when(noteRestService.getList(patient.getId())).thenReturn(noteGiven);

        //WHEN
        Risk risk = riskService.getRisk(patient.getLastName());

        //THEN
        assertNotNull(risk);
        assertEquals(RiskLevelEnum.EARLY_ONSET, risk.getRiskLevelEnum());
    }
}