package com.mediscreen.patient.service;

import com.mediscreen.patient.model.Patient;
import com.mediscreen.patient.repository.PatientRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientDalServiceBeanTest {

    @Mock
    private PatientRepository patientRepositoryMock;

    @InjectMocks
    private PatientDalServiceBean patientDalService;

    private Patient patientGiven;

    @BeforeAll
    void setUpAll(){
        patientGiven = new Patient("M","firstname1_Test","lastname1_Test",
                LocalDate.of(2020,5,19),"address1_Test","phone1_Test");
    }

    @Order(1)
    @Test
    void create() {
        //GIVEN
        when(patientRepositoryMock.save(patientGiven)).thenReturn(patientGiven);
        verify(patientRepositoryMock, Mockito.never()).save(patientGiven);

        //WHEN
        Patient patientResult = patientDalService.create(patientGiven);

        //THEN
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
        verify(patientRepositoryMock, Mockito.times(1)).save(patientGiven);
    }

    @Order(2)
    @Test
    void create_withNull() {
        //WHEN
        Exception exception = assertThrows(NullPointerException.class, () -> {
            patientDalService.create(null);
        });

        //THEN
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("patient is marked non-null but is null"));
        verify(patientRepositoryMock, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Order(3)
    @Test
    void findAll(){
        //GIVEN
        when(patientRepositoryMock.findAll()).thenReturn(List.of(patientGiven));

        //WHEN
        Collection<Patient> patientResult = patientDalService.findAll();

        //THEN
        assertNotNull(patientResult);
        assertEquals(1, patientResult.size());
        assertTrue(patientResult.contains(patientGiven));
        verify(patientRepositoryMock, Mockito.times(1)).findAll();
    }

    @Order(4)
    @Test
    void getPatient(){
        //GIVEN
        when(patientRepositoryMock.findById(ArgumentMatchers.anyInt())).thenReturn(java.util.Optional.ofNullable(patientGiven));

        //WHEN
        Patient patientResult = patientDalService.getPatient(3);

        //THEN
        assertNotNull(patientResult);
        assertEquals(patientGiven, patientResult);
        verify(patientRepositoryMock, Mockito.times(1)).findById(anyInt());
    }


}