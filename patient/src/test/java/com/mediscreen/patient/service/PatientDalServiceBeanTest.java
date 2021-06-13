package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PatientDalServiceBeanTest {

    @Mock
    private PatientRepository patientRepositoryMock;

    @InjectMocks
    private PatientDalServiceBean patientDalService;

    @Test
    void create() {
        //GIVEN
        Patient patientGiven = new Patient("M","firstname1_Test","lastname1_Test",
                LocalDate.of(2020,5,19),"address1_Test","phone1_Test");
        when(patientRepositoryMock.save(patientGiven)).thenReturn(patientGiven);
        verify(patientRepositoryMock, Mockito.never()).save(patientGiven);

        //WHEN
        Patient patientResult = patientDalService.create(patientGiven);

        //THEN
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
        verify(patientRepositoryMock, Mockito.times(1)).save(patientGiven);
    }
}