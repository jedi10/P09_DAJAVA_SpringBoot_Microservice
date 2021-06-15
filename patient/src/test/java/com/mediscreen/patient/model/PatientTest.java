package com.mediscreen.patient.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static pl.pojo.tester.api.assertion.Method.*;

class PatientTest {

    @Test
    void patientPojo(){
        //GIVEN
        final Class<?> classUnderTest = Patient.class;
        //WHEN-THEN
        assertPojoMethodsFor(classUnderTest)
                .testing(HASH_CODE)
                .testing(EQUALS)
                .testing(SETTER)
                .testing(GETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }

}