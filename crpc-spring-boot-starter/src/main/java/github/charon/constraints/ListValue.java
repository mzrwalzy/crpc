package github.charon.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}//这里指定的就是下面创建的验证器
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {
    String message() default "必须是指定的三种序列化工具中的一个 （hessian, kryo, protostuff）";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values() default {};
}
