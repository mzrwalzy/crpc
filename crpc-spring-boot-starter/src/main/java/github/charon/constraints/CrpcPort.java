package github.charon.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {CrpcPortConstraintsValidator.class}//这里指定的就是下面创建的验证器
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrpcPort {
    String message() default "不能与SpringBoot项目端口号一致";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
