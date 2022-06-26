package github.charon.constraints;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class CrpcPortConstraintsValidator implements ConstraintValidator<CrpcPort, Integer> {

    @Value("${server.port}")
    private Integer port;

    @Override
    public void initialize(CrpcPort constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer i, ConstraintValidatorContext constraintValidatorContext) {
        return !Objects.equals(i, port) && i <= 65535 && i >= 0;
    }
}
