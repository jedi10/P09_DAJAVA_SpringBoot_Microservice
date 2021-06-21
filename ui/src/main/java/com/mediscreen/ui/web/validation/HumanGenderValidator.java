package com.mediscreen.ui.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HumanGenderValidator implements ConstraintValidator<HumanGender, String> {

    private Pattern pattern = Pattern.compile("^[FMT]$");

    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        if (str == null) {
            return false;
        }
        if (pattern.matcher(str).matches()){
            return true;
        }
        //constraintValidatorContext.buildConstraintViolationWithTemplate("Entry is not numerical")
        //        .addConstraintViolation()
        //        .disableDefaultConstraintViolation();
        return false;
    }
}
