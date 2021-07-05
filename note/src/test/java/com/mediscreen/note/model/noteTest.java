package com.mediscreen.note.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static pl.pojo.tester.api.assertion.Method.*;
import static pl.pojo.tester.api.FieldPredicate.exclude;
import static org.junit.jupiter.api.Assertions.*;

class noteTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void notePojo(){
        //GIVEN
        final Class<?> classUnderTest = Note.class;
        //WHEN-THEN
        assertPojoMethodsFor(classUnderTest, exclude("id"))
                .testing(HASH_CODE)
                .testing(EQUALS)
                .areWellImplemented();
        assertPojoMethodsFor(classUnderTest)
                .testing(SETTER)
                .testing(GETTER)
                .testing(CONSTRUCTOR)
                .areWellImplemented();
    }
}