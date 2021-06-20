package com.mediscreen.ui.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <b>Custom Annotation for Human Gender Validation</b>
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = HumanGenderValidator.class)
@Documented
public @interface HumanGender {
    String message() default "Entry have to be one letter F, M or T";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
