package com.mediscreen.ui.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static pl.pojo.tester.api.FieldPredicate.exclude;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;
import static pl.pojo.tester.api.assertion.Method.*;

class NoteTest {

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