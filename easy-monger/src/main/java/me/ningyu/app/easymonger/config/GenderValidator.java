package me.ningyu.app.easymonger.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.ningyu.app.easymonger.model.enums.Gender;

public class GenderValidator implements ConstraintValidator<ValidGender, Gender>
{
    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext constraintValidatorContext)
    {
        return value == Gender.MALE || value == Gender.FEMALE;
    }
}
