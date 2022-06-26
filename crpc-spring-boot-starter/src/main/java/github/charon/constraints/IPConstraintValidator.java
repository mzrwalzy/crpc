package github.charon.constraints;

import github.charon.constraints.IP;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IPConstraintValidator implements ConstraintValidator<IP, String> {
    @Override
    public void initialize(IP constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        String pattern = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$";
        boolean isMatch = Pattern.matches(pattern, s);
        if (isMatch) {
            String[] split = s.split("\\.");
            for (String s1 : split) {
                if (s1.startsWith("0") && s1.length() != 1) {
                    isMatch = false;
                    break;
                }
            }
        }
        return isMatch;
    }
}
