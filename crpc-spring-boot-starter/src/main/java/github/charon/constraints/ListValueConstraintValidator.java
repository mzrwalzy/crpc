package github.charon.constraints;

import github.charon.constraints.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, String> {

    private final Set<String> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        set.addAll(Arrays.asList(constraintAnnotation.values()));
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(s);
    }
}
